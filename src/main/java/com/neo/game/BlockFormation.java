package com.neo.game;

public class BlockFormation {
    public static final BlockFormation I_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, true, true, true}
            },
            Block.Color.Cyan
    );
    public static final BlockFormation J_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, false, false, true},
                    {true, true, true, true}
            },
            Block.Color.Blue
    );
    public static final BlockFormation L_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, false, false, false},
                    {true, true, true, true}
            },
            Block.Color.Orange
    );
    public static final BlockFormation S_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, true, true},
                    {true, true, false}
            },
            Block.Color.Yellow
    );
    public static final BlockFormation T_BLOCK = new BlockFormation(
            new boolean[][]{
                    {false, true, false},
                    {true, true, true}
            },
            Block.Color.Purple
    );
    public static final BlockFormation Z_BLOCK = new BlockFormation(
            new boolean[][]{
                    {true, true, false},
                    {false, true, true}
            },
            Block.Color.Red
    );

    /*public static final BlockFormation O_BLOCK = new BlockFormation(
            new boolean[][] {

            },
            Block.Color.Yellow
    );*/
    public static final BlockFormation[] ALL = new BlockFormation[]{
            I_BLOCK,
            J_BLOCK,
            L_BLOCK,
            S_BLOCK,
            T_BLOCK,
            Z_BLOCK
    };
    boolean[][] pattern;
    Block.Color color;

    public BlockFormation(boolean[][] pattern, Block.Color color) {
        this.pattern = pattern;
        this.color = color;
    }

    public boolean[][] getPattern() {
        return pattern;
    }

    public Block.Color getColor() {
        return color;
    }
}
