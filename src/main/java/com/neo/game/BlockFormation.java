package com.neo.game;

/**
 * Represents the formation of an individual Tetromino
 */
public class BlockFormation {
    /**
     * Represents the formation of the I block Tetromino
     */
    public static final BlockFormation I_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, true, true, true}
            },
            Block.Color.Cyan
    );

    /**
     * Represents the formation of the J block Tetromino
     */
    public static final BlockFormation J_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, false, false, true},
                    {true, true, true, true}
            },
            Block.Color.Blue
    );

    /**
     * Represents the formation of the L block Tetromino
     */
    public static final BlockFormation L_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, false, false, false},
                    {true, true, true, true}
            },
            Block.Color.Orange
    );

    /**
     * Represents the formation of the S block Tetromino
     */
    public static final BlockFormation S_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, true, true},
                    {true, true, false}
            },
            Block.Color.Yellow
    );

    /**
     * Represents the formation of the T block Tetromino
     */
    public static final BlockFormation T_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, true, false},
                    {true, true, true}
            },
            Block.Color.Purple
    );

    /**
     * Represents the formation of the Z block Tetromino
     */
    public static final BlockFormation Z_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, true, false},
                    {false, true, true}
            },
            Block.Color.Red
    );

    public static final BlockFormation O_BLOCK = new BlockFormation(
            new boolean[][] {
                    {true, true},
                    {true, true}
            },
            Block.Color.Green
    );
    /**
     * Represents all preset Tetromino formations
     */
    public static final BlockFormation[] ALL = new BlockFormation[]{
            I_BLOCK,
            J_BLOCK,
            L_BLOCK,
            S_BLOCK,
            T_BLOCK,
            Z_BLOCK,
            O_BLOCK
    };
    boolean[][] pattern;
    Block.Color color;

    private BlockFormation(boolean[][] pattern, Block.Color color) {
        this.pattern = pattern;
        this.color = color;
    }

    /**
     * Gets the pattern/shape of this formation
     *
     * @return Pattern/shape of formation
     */
    public boolean[][] getPattern() {
        return pattern;
    }

    /**
     * Gets the color of this formation
     *
     * @return Color of formation
     */
    public Block.Color getColor() {
        return color;
    }
}
