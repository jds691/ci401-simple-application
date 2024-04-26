package com.neo.game;

import com.neo.twig.scene.NodeComponent;
import javafx.scene.paint.Color;

// When checking moving blocks colliding with static ones, update from bottom to top

/**
 * Contains the state for the game board at any given time.
 */
public class BoardDataComponent extends NodeComponent {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private boolean pauseUpdates;

    private Block[][] boardState;

    @Override
    public void start() {
        super.start();

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
    }

    private void checkCollisions() {

    }

    public boolean checkLineClear(int y) {
        for (int i = 0; i <= BOARD_WIDTH; i++) {
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
