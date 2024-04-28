package com.neo.game;

import com.neo.game.input.InputAction;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.audio.AudioService;
import com.neo.twig.input.InputService;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Contains the state for the game board at any given time.
 */
public class BoardDataComponent extends NodeComponent {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private AudioService audioService;

    private String lineClearAudioKey;
    private String blockPlaceAudioKey;

    private AudioPlayer lineClearSfx;
    private AudioPlayer blockPlaceSfx;

    private InputService inputService;
    private SceneService sceneService;

    @ForceSerialize
    private double movementDelay;
    private double currentMovementDelay;
    private boolean pauseUpdates;

    private Block[][] boardState;

    @Override
    public void start() {
        super.start();

        currentMovementDelay = movementDelay;

        audioService = Engine.getAudioService();
        inputService = Engine.getInputService();
        sceneService = Engine.getSceneService();

        boardState = new Block[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                boardState[i][j] = new Block();
            }
        }

        boardState[0][0].isMoving = true;
        boardState[0][0].color = Block.Color.Blue;

        boardState[1][1].isMoving = true;
        boardState[1][1].color = Block.Color.Cyan;

        boardState[1][0].isMoving = true;
        boardState[1][0].color = Block.Color.Orange;

        boardState[0][1].isMoving = true;
        boardState[0][1].color = Block.Color.Purple;

        boardState[0][2].isMoving = true;
        boardState[0][2].color = Block.Color.Red;

        boardState[5][2].isMoving = false;
        boardState[5][2].color = Block.Color.Yellow;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        /*if (inputService.wasKeyPressed(KeyCode.ENTER)) {
            pauseUpdates = false;
        }*/

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

        boolean blocksNeedMoved;
        boolean isMovingRight;
        if (InputAction.MOVE_LEFT.isActivationHeld()) {
            blocksNeedMoved = true;
        } else if (InputAction.MOVE_RIGHT.isActivationHeld()) {
            blocksNeedMoved = true;
            isMovingRight = true;
        }

        if (currentMovementDelay > 0) {
            currentMovementDelay -= deltaTime;
            return;
        } else {
            currentMovementDelay = movementDelay;
        }

        boolean didCollisionOccur = false;
        // Values are added as pairs, first = x second = y
        ArrayList<Pair<Integer, Integer>> queuedMovement = new ArrayList<>();

        // Perform updates backwards since moving blocks need to be moved in the correct order
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

        // If any of the moving blocks met a collision, prevent any other moving blocks being moved
        if (!didCollisionOccur) {
            // Reverse the loop yet again since movement is queued in reverse in the loop above
            for (int i = 0; i < queuedMovement.size(); i++) {
                int x = queuedMovement.get(i).getKey();
                int y = queuedMovement.get(i).getValue();

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

            if (!linesCleared.isEmpty())
                lineClearSfx.play();

            // Request the next block be created
        }

        //pauseUpdates = true;
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
