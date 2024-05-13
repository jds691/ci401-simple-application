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

    public Block(Color color, int tone) {
        this.color = color;
        this.tone = tone;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getTone() {
        return tone;
    }

    public void setTone(int tone) {
        this.tone = tone;
    }

    public boolean getIsMoving() {
        return isMoving;
    }

    public void setIsMoving(boolean moving) {
        isMoving = moving;
    }
}
