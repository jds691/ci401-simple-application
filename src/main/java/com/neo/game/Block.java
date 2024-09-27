package com.neo.game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Structure containing the necessary data to render and move a block object on screen
 */
public class Block {
    Color color;
    int tone;
    boolean isMoving;

    /**
     * Constructs a stationary, invisible block with a random tone
     */
    public Block() {
        color = Color.None; //For some reason enums default to null
        tone = ThreadLocalRandom.current().nextInt(0, 3);
        isMoving = false;
    }

    /**
     * Creates a block with the specified color and tone
     *
     * @param color Color to initially start with
     * @param tone  Tone to initially start with
     */
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
        Yellow,
        Green
    }

    /**
     * Gets the current color of the block
     *
     * @return Current color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the current color of the block
     *
     * @param color Color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the current tone of the block
     *
     * @return Current tone (0-2)
     */
    public int getTone() {
        return tone;
    }

    /**
     * Sets the current tone of the block
     *
     * @param tone Tone to set (0-2)
     */
    public void setTone(int tone) {
        this.tone = tone;
    }

    /**
     * Gets if the block should be moving
     *
     * @return If the block should be moving
     */
    public boolean getIsMoving() {
        return isMoving;
    }

    /**
     * Sets if the block should be moving
     *
     * @param moving Movement state of the block
     */
    public void setIsMoving(boolean moving) {
        isMoving = moving;
    }
}
