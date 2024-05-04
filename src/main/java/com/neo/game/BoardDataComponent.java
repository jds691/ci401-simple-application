package com.neo.game;

import com.neo.game.audio.SoundConfig;
import com.neo.game.input.InputAction;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.audio.AudioService;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;
import javafx.util.Pair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Contains the state for the game board at any given time.
 */
public class BoardDataComponent extends NodeComponent {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private AudioService audioService;

    private final String lineClearAudioKey = "SFX_lineClear";
    private final String blockPlaceAudioKey = "SFX_blockPlace";

    private AudioPlayer lineClearSfx;
    private AudioPlayer blockPlaceSfx;

    private GameManager gameManager;

    private SceneService sceneService;

    @ForceSerialize
    private double movementDelay;
    private double currentMovementDelay;
    private boolean pauseUpdates;

    private ArrayList<Pair<Integer, Integer>> queuedMovement;

    private Block[][] boardState;

    private boolean needsNewBlockSpawn;
    private RotationState currentRotationState = RotationState.ZERO;
    private BlockFormation pendingBlock;

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
            pauseUpdates = paused;
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

        boardState = new Block[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                boardState[i][j] = new Block();
            }
        }

        pendingBlock = gameManager.requestRandomBlockFormation();
        needsNewBlockSpawn = true;
    }

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
        boolean blocksNeedRotated = false;
        boolean isRotatingRight = false;
        if (InputAction.MOVE_LEFT.wasActivatedThisFrame()) {
            blocksNeedRotated = true;
        } else if (InputAction.ROTATE_RIGHT.wasActivatedThisFrame()) {
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
        if (InputAction.MOVE_LEFT.wasActivatedThisFrame()) {
            blocksNeedMoved = true;
        } else if (InputAction.MOVE_RIGHT.wasActivatedThisFrame()) {
            blocksNeedMoved = true;
            isMovingRight = true;
        }

        if (blocksNeedMoved) {
            handleHorizontalMovement(isMovingRight);
        }

        if (currentMovementDelay > 0) {
            currentMovementDelay -= deltaTime;
            return;
        } else {
            currentMovementDelay = movementDelay;
        }

        boolean didCollisionOccur;
        queuedMovement = new ArrayList<>();

        didCollisionOccur = checkDownMovementCollision();
        handleCollisions(didCollisionOccur);

        if (needsNewBlockSpawn) {
            int midPoint = (BOARD_WIDTH / 2) - 1;

            int maxBlockLength = pendingBlock.pattern[0].length;

            int halfMaxBlockLength = maxBlockLength / 2;

            int startingPoint = midPoint - halfMaxBlockLength;

            boolean boardFull = false;

            for (int i = 0; i < maxBlockLength; i++) {
                for (int j = 0; j < pendingBlock.pattern.length; j++) {
                    boolean blockExists = pendingBlock.pattern[j][i];

                    // Block is occupied
                    if (boardState[j][startingPoint + i].color != Block.Color.None) {
                        boardFull = true;
                        break;
                    }

                    boardState[j][startingPoint + i].isMoving = true;
                    boardState[j][startingPoint + i].color = blockExists ? pendingBlock.color : Block.Color.None;
                    boardState[j][startingPoint + i].tone = ThreadLocalRandom.current().nextInt(0, 3);
                }
            }

            needsNewBlockSpawn = false;

            if (boardFull) {
                gameManager.signalGameEnd();
            }
        }
    }

    //TODO: Redo now that block formations are cached during use
    private void handleBlockRotation(boolean isRotatingRight) {
        // Find all moving blocks to calculate dimensions
        // Store coordinates for later changes

        int blockWidth = 0;
        int blockHeight = 0;
        ArrayList<Pair<Integer, Integer>> movingCoordinates = new ArrayList<>();

        boolean blockWidthFound = false;
        boolean blockHeightFound = false;

        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Block block = boardState[y][x];

                // Continue until the first moving block is found
                // Once first one is found, start incrementing block values
                // - When a non-moving block is found again, store block height and do not change it
                // - Repeat the loops until the same occurs with the width

                if (blockWidth == 0 && blockHeight == 0 && !block.isMoving)
                    continue;

                if (block.isMoving) {
                    movingCoordinates.add(new Pair<>(x, y));
                    if (!blockHeightFound)
                        blockHeight++;

                    blockWidth++;
                } else {
                    // No longer moving but we have started finding dimensions
                    if (!blockHeightFound) {
                        blockHeightFound = true;
                        continue;
                    } else {
                        blockWidthFound = true;
                    }
                }

                if (blockHeightFound && blockWidthFound)
                    break;
            }

            if (blockHeightFound && blockWidthFound)
                break;
        }

        for (Pair<Integer, Integer> movingCoordinate : movingCoordinates) {
            int x = movingCoordinate.getKey();
            int y = movingCoordinate.getValue();

            /*
             * Take a 3 x 2 block
             * |
             * | _ _
             *
             * The transformations involved for rotating right are:
             *
             * (0,0) = (1, 0) = (+1, 0)
             * (0,1) = (0, 0) = (0, +1)
             * (1,1) = (0, 1) = (-1, 0)
             * (2,1) = (0, 2) = (-2, +1)
             */

            TransformationManager.requestCoordinateTransformations(pendingBlock, isRotatingRight, currentRotationState);
        }
    }

    private void handleHorizontalMovement(boolean isMovingRight) {
        boolean canMove = true;
        boolean reachedMovingBlock = false;

        if (isMovingRight) {
            for (int x = BOARD_WIDTH - 1; x >= 0; x--) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    Block block = boardState[y][x];

                    if (block.color == Block.Color.None || !block.isMoving)
                        continue;

                    reachedMovingBlock = true;

                    // If a moving block is at the rightmost edge
                    if (x == BOARD_WIDTH - 1) {
                        canMove = false;
                        // Blocks are already at rightmost edge
                        break;
                    } else if (boardState[y][x + 1].color != Block.Color.None) {
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

                    boardState[y][x + 1].color = boardState[y][x].color;
                    boardState[y][x + 1].tone = boardState[y][x].tone;
                    boardState[y][x + 1].isMoving = true;

                    boardState[y][x].color = Block.Color.None;
                    boardState[y][x].isMoving = false;
                }
            }
        } else {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    Block block = boardState[y][x];

                    if (block.color == Block.Color.None || !block.isMoving)
                        continue;

                    reachedMovingBlock = true;

                    // If a moving block is at the rightmost edge
                    if (x == 0) {
                        canMove = false;
                        // Blocks are already at rightmost edge
                        break;
                    } else if (boardState[y][x - 1].color != Block.Color.None) {
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

                    boardState[y][x - 1].color = boardState[y][x].color;
                    boardState[y][x - 1].tone = boardState[y][x].tone;
                    boardState[y][x - 1].isMoving = true;

                    boardState[y][x].color = Block.Color.None;
                    boardState[y][x].isMoving = false;
                }
            }
        }
    }

    // Perform updates backwards since moving blocks need to be moved in the correct order
    private boolean checkDownMovementCollision() {
        boolean didCollisionOccur = false;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Block block = boardState[i][j];

                if (block.color == Block.Color.None)
                    continue;

                // Has a collision already happened or if the current block is moving, is the one below it stationary and occupied
                if (block.isMoving && (i == BOARD_HEIGHT - 1 || boardState[i + 1][j].color != Block.Color.None && !boardState[i + 1][j].isMoving)) {
                    didCollisionOccur = true;
                    break;
                }

                // Is the current block moving and is the block below empty or moving
                if (block.isMoving && (boardState[i + 1][j].color == Block.Color.None || boardState[i + 1][j].isMoving)) {
                    queuedMovement.add(new Pair<>(j, i));
                }
            }

            if (didCollisionOccur)
                break;
        }

        return didCollisionOccur;
    }

    // If any of the moving blocks met a collision, prevent any other moving blocks being moved
    private void handleCollisions(boolean didCollisionOccur) {
        if (!didCollisionOccur) {
            // Reverse the loop yet again since movement is queued in reverse in the loop above
            for (Pair<Integer, Integer> coordinatePair : queuedMovement) {
                int x = coordinatePair.getKey();
                int y = coordinatePair.getValue();

                boardState[y + 1][x].color = boardState[y][x].color;
                boardState[y + 1][x].tone = boardState[y][x].tone;
                boardState[y + 1][x].isMoving = true;

                boardState[y][x].color = Block.Color.None;
                boardState[y][x].isMoving = false;
            }
        } else {
            for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    boardState[i][j].isMoving = false;
                }
            }

            ArrayList<Integer> linesCleared = new ArrayList<>(BOARD_HEIGHT);
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                if (checkLineClear(i))
                    linesCleared.add(i);
            }

            if (!linesCleared.isEmpty()) {
                lineClearSfx.play();
                for (int lineY : linesCleared) {
                    for (int i = 0; i < BOARD_WIDTH; i++) {
                        boardState[lineY][i].color = Block.Color.None;
                        boardState[lineY][i].isMoving = false;
                    }
                }
            } else {
                blockPlaceSfx.play();
            }

            // Request the next block be created
            pendingBlock = gameManager.requestNextBlockFormationInQueue();
            needsNewBlockSpawn = true;
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
}
