package com.neo.game;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.audio.AudioService;
import com.neo.twig.input.InputService;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;

import java.util.ArrayList;

// When checking moving blocks colliding with static ones, update from bottom to top

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
    private float movementDelay;
    private boolean pauseUpdates;

    private Block[][] boardState;

    @Override
    public void start() {
        super.start();

        audioService = Engine.getAudioService();
        inputService = Engine.getInputService();
        sceneService = Engine.getSceneService();

        boardState = new Block[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                boardState[i][j] = new Block();
            }
        }
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

        ArrayList<Integer> linesCleared = new ArrayList<>(BOARD_HEIGHT);
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            if (checkLineClear(i))
                linesCleared.add(i);
        }

        if (!linesCleared.isEmpty())
            lineClearSfx.play();
    }

    private void checkCollisions() {

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
