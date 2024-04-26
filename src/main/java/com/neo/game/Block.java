package com.neo.game;

public class Block {
    Color color;
    boolean isMoving;

    public Block() {
        color = Color.None; //For some reason enums default to null
        isMoving = false;
    }

    public enum Color {
        None,
        Red,
        Orange,
        Yellow,
        Blue,
        Cyan
    }
}
