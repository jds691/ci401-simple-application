package com.neo.game;

public class Block {
    Color color;
    int tone;
    boolean isMoving;

    public Block() {
        color = Color.None; //For some reason enums default to null
        tone = 0;
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
