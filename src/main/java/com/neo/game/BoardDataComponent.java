package com.neo.game;

import com.neo.twig.scene.NodeComponent;

/**
 * Contains the state for the game board at any given time.
 */
public class BoardDataComponent extends NodeComponent {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private int[][] boardState;

    @Override
    public void start() {
        super.start();

        boardState = new int[BOARD_HEIGHT][BOARD_WIDTH];
    }

    private void checkCollisions() {

    }

    public boolean checkLineClear(int y) {
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            if (getBoardState(i, y) == 0) {
                return false;
            }
        }

        return true;
    }

    public int getBoardState(int x, int y) {
        return boardState[y][x];
    }
}
