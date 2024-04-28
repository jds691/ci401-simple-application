package com.neo.game;

import java.util.concurrent.ThreadLocalRandom;

public class Block {
    Color color;
    int tone;
    boolean isMoving;

    public Block() {
        color = Color.None; //For some reason enums default to null
        tone = ThreadLocalRandom.current().nextInt(0, 3);
        isMoving = false;
    }

    public enum Color {
        None,
        Blue,
        Cyan,
        Orange,
        Purple,
        Red,
        Yellow
    }
}
