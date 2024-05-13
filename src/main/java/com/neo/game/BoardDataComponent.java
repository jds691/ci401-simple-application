package com.neo.game;

import com.neo.game.audio.SoundConfig;
import com.neo.game.input.Input;
import com.neo.game.input.InputAction;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.audio.AudioService;
import com.neo.twig.events.Event;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;
import javafx.util.Pair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

//TODO: Potentially implement final destination guide, similar to Puyo Puyo?

/**
 * Contains the state for the game board at any given time.
 */
public class BoardDataComponent extends NodeComponent {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private AudioService audioService;

    private final String lineClearAudioKey = "SFX_lineClear";
    private final String blockPlaceAudioKey = "SFX_blockPlace";
    private final String blockRotateAudioKey = "SFX_blockRotate";
    private final String blockRotateInvalidKey = "SFX_Quit";

    private AudioPlayer lineClearSfx;
    private AudioPlayer blockPlaceSfx;
    private AudioPlayer blockRotateSfx;
    private AudioPlayer blockRotateInvalidSfx;

    private GameManager gameManager;

    private SceneService sceneService;

    @ForceSerialize
    private double movementDelay;
    private double currentMovementDelay;
    private boolean pauseUpdates;

    public Event<ArrayList<Integer>> linesDidClearEvent = new Event<>();

    @ForceSerialize
    private double speedUpFactor;

    private ArrayList<Pair<Integer, Integer>> queuedMovement;

    private Block[][] boardState;

    private boolean needsNewBlockSpawn;
    private RotationState currentRotationState = RotationState.ZERO;
    private BlockFormation pendingBlock;
    private boolean pausedByPauseEvent = false;
    private ArrayList<Integer> clearedLines = new ArrayList<>(BOARD_HEIGHT);

    @ForceSerialize
    private boolean debug_NonUniformColours = false;

    @Override
    public void start() {
        super.start();

        currentMovementDelay = movementDelay;

        audioService = Engine.getAudioService();
        sceneService = Engine.getSceneService();

        gameManager = sceneService.getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        gameManager.getPauseDidChangeEvent().addHandler((paused) -> {
            if (!pausedByPauseEvent && pauseUpdates)
                return;

            pausedByPauseEvent = true;
            pauseUpdates = paused;
        });
        gameManager.getGameDidEndEvent().addHandler((ignored) -> {
            pauseUpdates = true;
        });

        try {
            lineClearSfx = audioService.createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(lineClearAudioKey).toURI());
        } catch (URISyntaxException e) {
            lineClearSfx = null;
        }

        try {
            blockPlaceSfx = audioService.createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(blockPlaceAudioKey).toURI());
        } catch (URISyntaxException e) {
            blockPlaceSfx = null;
        }

        try {
            blockRotateSfx = audioService.createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(blockRotateAudioKey).toURI());
        } catch (URISyntaxException e) {
            blockRotateSfx = null;
        }

        try {
            blockRotateInvalidSfx = audioService.createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(blockRotateInvalidKey).toURI());
        } catch (URISyntaxException e) {
            blockRotateInvalidSfx = null;
        }

        boardState = new Block[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                boardState[i][j] = new Block();
                // Uncomment to force a game over on start
                //boardState[i][j].color = Block.Color.values()[ThreadLocalRandom.current().nextInt(1, Block.Color.values().length)];
            }
        }

        // Forces a line clear on game start
        /*for (int j = 0; j < BOARD_WIDTH - 1; j++) {
            boardState[19][j].color = Block.Color.Red;
        }
        boardState[18][BOARD_WIDTH - 1].color = Block.Color.Cyan;
        boardState[18][BOARD_WIDTH - 1].isMoving = true;
        boardState[17][BOARD_WIDTH - 1].color = Block.Color.Cyan;
        boardState[17][BOARD_WIDTH - 1].isMoving = true;*/

        //boardState[8][6].color = Block.Color.Red;

        pendingBlock = gameManager.requestRandomBlockFormation();
        needsNewBlockSpawn = true;
    }

    //TODO: Be able to clear lines without the movement delay
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (pauseUpdates)
            return;

        /*
         * Tasks:
         * - Run input updates
         * - If there are moving blocks, move them and check collisions
         *   - If collision, isMoving = false. Check line clears, play click SFX
         *       - If line clears, play SFX, trigger render transition, pause updates and await completion
         *   - Wait for movement time
         *   - Spawn new block shape, repeat
         * - If checkLineClear for y = 0 is true, end game
         */
        if (!needsNewBlockSpawn) {
            boolean blocksNeedRotated = false;
            boolean isRotatingRight = false;
            if (InputAction.get(Input.ROTATE_LEFT).wasActivatedThisFrame()) {
                blocksNeedRotated = true;
            } else if (InputAction.get(Input.ROTATE_RIGHT).wasActivatedThisFrame()) {
                blocksNeedRotated = true;
                isRotatingRight = true;
            }

            if (blocksNeedRotated) {
                handleBlockRotation(isRotatingRight);
            }

            // Filter to blocks where isMoving = true. Rotate in place based on overall shape
            // Could be done by temporarily cloning the board state?

            boolean blocksNeedMoved = false;
            boolean isMovingRight = false;
            if (InputAction.get(Input.MOVE_LEFT).wasActivatedThisFrame()) {
                blocksNeedMoved = true;
            } else if (InputAction.get(Input.MOVE_RIGHT).wasActivatedThisFrame()) {
                blocksNeedMoved = true;
                isMovingRight = true;
            }

            if (blocksNeedMoved) {
                handleHorizontalMovement(isMovingRight);
            }

            if (currentMovementDelay > 0) {
                if (InputAction.get(Input.MOVE_DOWN).isActivationHeld())
                    currentMovementDelay -= deltaTime * speedUpFactor;
                else
                    currentMovementDelay -= deltaTime;

                return;
            } else {
                currentMovementDelay = movementDelay;
            }
        }

        queuedMovement = new ArrayList<>();

        if (!clearedLines.isEmpty())
            handleClearedLines();

        if (needsNewBlockSpawn) {
            currentRotationState = RotationState.ZERO;
            int midPoint = (BOARD_WIDTH / 2) - 1;

            int maxBlockLength = pendingBlock.pattern[0].length;

            int halfMaxBlockLength = maxBlockLength / 2;

            int startingPoint = midPoint - halfMaxBlockLength;

            boolean boardFull = false;

            Block.Color startColour = Block.Color.Blue;
            for (int i = 0; i < maxBlockLength; i++) {
                for (int j = 0; j < pendingBlock.pattern.length; j++) {
                    boolean blockExists = pendingBlock.pattern[j][i];

                    // Block is occupied
                    //TODO: Check ALL coordinates before placing blocks
                    if (boardState[j][startingPoint + i].color != Block.Color.None && !boardState[j][startingPoint + i].isMoving) {
                        boardFull = true;
                        break;
                    }

                    boardState[j][startingPoint + i].isMoving = true;

                    if (debug_NonUniformColours) {
                        boardState[j][startingPoint + i].color = blockExists ? startColour : Block.Color.None;

                        if (!blockExists)
                            continue;

                        int currentColorOrdinal = startColour.ordinal();
                        currentColorOrdinal++;

                        if (currentColorOrdinal > Block.Color.values().length)
                            currentColorOrdinal = 0;

                        startColour = Block.Color.values()[currentColorOrdinal];
                    } else {
                        boardState[j][startingPoint + i].color = blockExists ? pendingBlock.color : Block.Color.None;
                    }

                    boardState[j][startingPoint + i].tone = ThreadLocalRandom.current().nextInt(0, 3);
                }
            }

            needsNewBlockSpawn = false;

            if (boardFull) {
                gameManager.signalGameEnd();
            }
        }

        handleCollisions(checkDownMovementCollision());
    }

    private void handleBlockRotation(boolean isRotatingRight) {
        // Find all moving blocks to calculate dimensions
        // Store coordinates for later changes
        ArrayList<Pair<Integer, Integer>> movingCoordinates = new ArrayList<>();

        Block[][] blockMatrix;
        Block[][] blockTransposedMatrix;

        int length;
        int height;

        if (isCurrentRotationVertical()) {
            length = pendingBlock.getPattern().length;
            height = pendingBlock.getPattern()[0].length;
        } else {
            length = pendingBlock.getPattern()[0].length;
            height = pendingBlock.getPattern().length;
        }

        blockMatrix = new Block[length][height];
        blockTransposedMatrix = new Block[height][length];

        int startX = -1;
        int startY = -1;

        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Block block = boardState[y][x];

                if (block.isMoving) {
                    if (startX == -1)
                        startX = x;

                    if (startY == -1)
                        startY = y;

                    movingCoordinates.add(new Pair<>(x, y));
                    blockMatrix[x - startX][y - startY] = block;
                }
            }
        }

        for (int x = 0; x < blockMatrix.length; x++) {
            for (int y = 0; y < blockMatrix[x].length; y++) {
                if (blockMatrix[x][y] == null)
                    blockMatrix[x][y] = new Block();
            }
        }

        // Matrix Transposition
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                blockTransposedMatrix[y][x] = blockMatrix[x][y];
            }
        }

        if (isRotatingRight) {
            // Reverse order of columns on transposed
            for (int x = 0; x < blockTransposedMatrix.length / 2; x++) {
                Block[] temp = blockTransposedMatrix[x];
                blockTransposedMatrix[x] = blockTransposedMatrix[blockTransposedMatrix.length - x - 1];
                blockTransposedMatrix[blockTransposedMatrix.length - x - 1] = temp;
            }
        } else {
            // Reverse order of rows on transposed
            for (int x = 0; x < blockTransposedMatrix.length; x++) {
                for (int y = 0; y < blockTransposedMatrix[x].length / 2; y++) {
                    Block temp = blockTransposedMatrix[x][y];
                    blockTransposedMatrix[x][y] = blockTransposedMatrix[x][blockTransposedMatrix[x].length - y - 1];
                    blockTransposedMatrix[x][blockTransposedMatrix[x].length - y - 1] = temp;
                }
            }
        }

        boolean rotationValid = true;
        for (int x = 0; x < blockTransposedMatrix.length; x++) {
            for (int y = 0; y < blockTransposedMatrix[x].length; y++) {
                int newX = x + startX;
                int newY = y + startY;

                // Off screen checks
                if (
                        newX > BOARD_WIDTH - 1 ||
                                newX < 0 ||
                                newY > BOARD_HEIGHT - 1 ||
                                newY < 0
                ) {
                    // The rotation is invalid and should be ignored
                    rotationValid = false;
                    break;
                }

                // Occupancy checks
                if (boardState[newY][newX].color != Block.Color.None && !movingCoordinates.contains(new Pair<>(newX, newY))) {
                    // The rotation is invalid and should be ignored
                    rotationValid = false;
                    break;
                }
            }
        }

        if (!rotationValid) {
            blockRotateInvalidSfx.play();
            return;
        }

        if (isRotatingRight) {
            int currentOrdinal = currentRotationState.ordinal();
            currentOrdinal++;

            if (currentOrdinal == RotationState.values().length)
                currentOrdinal = 0;

            currentRotationState = RotationState.values()[currentOrdinal];
        } else {
            int currentOrdinal = currentRotationState.ordinal();
            currentOrdinal--;

            if (currentOrdinal == -1)
                currentOrdinal = RotationState.values().length - 1;

            currentRotationState = RotationState.values()[currentOrdinal];
        }

        for (Pair<Integer, Integer> coord : movingCoordinates) {
            int y = coord.getValue();
            int x = coord.getKey();

            boardState[y][x] = new Block();
        }

        for (int x = 0; x < blockTransposedMatrix.length; x++) {
            for (int y = 0; y < blockTransposedMatrix[x].length; y++) {
                int newX = x + startX;
                int newY = y + startY;

                boardState[newY][newX].color = blockTransposedMatrix[x][y].color;
                boardState[newY][newX].tone = blockTransposedMatrix[x][y].tone;
                boardState[newY][newX].isMoving = true;
            }
        }

        //blockRotateSfx.play();
    }

    // TODO: Moving blocks horizontally causes blocks to get erased if a moving but invisble block hits another
    private void handleHorizontalMovement(boolean isMovingRight) {
        boolean canMove = true;
        boolean reachedMovingBlock = false;

        if (isMovingRight) {
            for (int x = BOARD_WIDTH - 1; x >= 0; x--) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    Block block = boardState[y][x];

                    if (!block.isMoving)
                        continue;

                    reachedMovingBlock = true;

                    // If a moving block is at the rightmost edge
                    if (x == BOARD_WIDTH - 1) {
                        canMove = false;
                        // Blocks are already at rightmost edge
                        break;
                    } else if (boardState[y][x].color != Block.Color.None && boardState[y][x + 1].color != Block.Color.None) {
                        // Once a moving block is reached, no columns past the current one will be checked
                        canMove = false;
                        break;
                    }
                }

                if (reachedMovingBlock)
                    break;
            }

            if (!canMove)
                return;

            // -2 because if a block were at the right most edge this wouldn't be running anyway
            for (int x = BOARD_WIDTH - 2; x >= 0; x--) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    if (!boardState[y][x].isMoving)
                        continue;

                    if (boardState[y][x].color != Block.Color.None) {
                        boardState[y][x + 1].color = boardState[y][x].color;
                        boardState[y][x + 1].tone = boardState[y][x].tone;
                        boardState[y][x + 1].isMoving = true;
                    } else if (boardState[y][x].color == Block.Color.None && boardState[y][x + 1].color == Block.Color.None) {
                        boardState[y][x + 1].isMoving = true;
                    }

                    boardState[y][x].color = Block.Color.None;
                    boardState[y][x].isMoving = false;
                }
            }
        } else {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    Block block = boardState[y][x];

                    if (!block.isMoving)
                        continue;

                    reachedMovingBlock = true;

                    // If a moving block is at the rightmost edge
                    if (x == 0) {
                        canMove = false;
                        // Blocks are already at rightmost edge
                        break;
                    } else if (boardState[y][x].color != Block.Color.None && boardState[y][x - 1].color != Block.Color.None) {
                        // Once a moving block is reached, no columns past the current one will be checked
                        canMove = false;
                        break;
                    }
                }

                if (reachedMovingBlock)
                    break;
            }

            if (!canMove)
                return;

            // -2 because if a block were at the right most edge this wouldn't be running anyway
            for (int x = 1; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    if (!boardState[y][x].isMoving)
                        continue;

                    if (boardState[y][x].color != Block.Color.None) {
                        boardState[y][x - 1].color = boardState[y][x].color;
                        boardState[y][x - 1].tone = boardState[y][x].tone;
                        boardState[y][x - 1].isMoving = true;
                    } else if (boardState[y][x].color == Block.Color.None && boardState[y][x - 1].color == Block.Color.None) {
                        boardState[y][x - 1].isMoving = true;
                    }

                    boardState[y][x].color = Block.Color.None;
                    boardState[y][x].isMoving = false;
                }
            }
        }
    }

    // Perform updates backwards since moving blocks need to be moved in the correct order
    private boolean checkDownMovementCollision() {
        boolean didCollisionOccur = false;

        /*for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Block block = boardState[i][j];

                //if (block.color == Block.Color.None)
                //    continue;

                // Examples:
                // - Erasing parts of the current block (Usually leads to out of bounds errors)

                // If the current block is moving, is the one below it stationary and occupied
                if (block.color != Block.Color.None && block.isMoving && (i == BOARD_HEIGHT - 1 || boardState[i + 1][j].color != Block.Color.None && !boardState[i + 1][j].isMoving)) {
                    didCollisionOccur = true;
                    break;
                }

                // Is the current block moving and is the block below empty or moving
                if (block.isMoving && (boardState[i + 1][j].color == Block.Color.None || boardState[i + 1][j].isMoving)) {
                    queuedMovement.add(new Pair<>(j, i));
                }
            }*/

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Block block = boardState[i][j];

                if (block.isMoving)
                    queuedMovement.add(new Pair<>(j, i));
            }
        }

        // These coordinates start from bottom right, moving left. Then up a row
        for (Pair<Integer, Integer> movingCoordinates : queuedMovement) {
            int x = movingCoordinates.getKey();
            int y = movingCoordinates.getValue();

            if (y == BOARD_HEIGHT - 1) {
                didCollisionOccur = true;
                break;
            }

            // If block below isn't moving and visible, collision
            // Only if a visible block will collide with it
            if (!boardState[y + 1][x].isMoving && boardState[y + 1][x].color != Block.Color.None && boardState[y][x].color != Block.Color.None) {
                didCollisionOccur = true;
                break;
            }
        }

        return didCollisionOccur;
    }

    // If any of the moving blocks met a collision, prevent any other moving blocks being moved
    private void handleCollisions(boolean didCollisionOccur) {
        if (!didCollisionOccur) {
            clearedLines.clear();
            for (Pair<Integer, Integer> coordinatePair : queuedMovement) {
                int x = coordinatePair.getKey();
                int y = coordinatePair.getValue();

                if (boardState[y][x].color != Block.Color.None) {
                    boardState[y + 1][x].color = boardState[y][x].color;
                    boardState[y + 1][x].tone = boardState[y][x].tone;
                    boardState[y + 1][x].isMoving = true;
                } else if (boardState[y][x].color == Block.Color.None && boardState[y + 1][x].color == Block.Color.None) {
                    boardState[y + 1][x].isMoving = true;
                }

                boardState[y][x].color = Block.Color.None;
                boardState[y][x].isMoving = false;
            }
        } else {
            for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    boardState[i][j].isMoving = false;
                }
            }

            for (int i = 0; i < BOARD_HEIGHT; i++) {
                if (checkLineClear(i))
                    clearedLines.add(i);
            }

            if (!clearedLines.isEmpty()) {
                linesDidClearEvent.emit(clearedLines);
                lineClearSfx.play();

                // There is a lot more involved in the scoring system which I would like to add in the future
                // You can see this here: https://tetris.wiki/Scoring
                // Due to time constraints, I am adding a very simple scoring system
                // This also means this edition of Tetris doesn't conform to the official standards which I would like to do in the future
                // But oh well

                // This edition of Tetris also uses a fixed gravity speed (I didn't know it could change)

                int awardedScore;

                switch (clearedLines.size()) {
                    case 1:
                        awardedScore = 100;
                        break;
                    case 2:
                        awardedScore = 300;
                        break;
                    case 3:
                        awardedScore = 500;
                        break;
                    default:
                        awardedScore = 800;
                }

                gameManager.addScore(awardedScore);
            } else {
                blockPlaceSfx.play();
            }

            // Request the next block be created
            pendingBlock = gameManager.requestNextBlockFormationInQueue();
            needsNewBlockSpawn = true;
        }
    }

    /**
     * Shifts the board downwards
     *
     * @param y The line to shift downward from (this line should be clear)
     */
    private void shiftBoard(int y) {
        for (int i = y - 1; i > 0; i--) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                boardState[i + 1][x].color = boardState[i][x].color;
                boardState[i][x].color = Block.Color.None;
            }
        }
    }

    private void handleClearedLines() {
        for (int lineY : clearedLines) {
            for (int i = 0; i < BOARD_WIDTH; i++) {
                boardState[lineY][i].color = Block.Color.None;
                boardState[lineY][i].isMoving = false;
            }
        }

        boolean boardShiftRequired = true;
        while (boardShiftRequired) {
            for (int y = BOARD_HEIGHT - 1; y > 0; y--) {
                boolean lineIsEmpty = true;
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    if (boardState[y][x].color != Block.Color.None) {
                        lineIsEmpty = false;
                        break;
                    }
                }

                if (lineIsEmpty) {
                    boolean restOfBoardEmpty = true;
                    for (int y2 = y; y2 > 0; y2--) {
                        for (int x = 0; x < BOARD_WIDTH; x++) {
                            if (boardState[y2][x].color != Block.Color.None) {
                                restOfBoardEmpty = false;
                                break;
                            }
                        }
                    }

                    if (!restOfBoardEmpty) {
                        shiftBoard(y);
                        // Research the whole board again
                        y = BOARD_HEIGHT;
                    }
                }
            }

            boardShiftRequired = false;
        }
    }

    public boolean checkLineClear(int y) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            if (getBoardState(i, y).color == Block.Color.None) {
                return false;
            }
        }

        return true;
    }

    public Block getBoardState(int x, int y) {
        return boardState[y][x];
    }

    public void setPauseUpdates(boolean pause) {
        pausedByPauseEvent = false;
        pauseUpdates = pause;
    }

    public Event<ArrayList<Integer>> getLinesDidClearEvent() {
        return linesDidClearEvent;
    }

    private boolean isCurrentRotationVertical() {
        return currentRotationState == RotationState.NINETY || currentRotationState == RotationState.TWO_HUNDRED_SEVENTY;
    }
}
