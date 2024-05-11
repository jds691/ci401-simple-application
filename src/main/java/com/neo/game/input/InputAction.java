package com.neo.game.input;

import com.neo.twig.Engine;
import com.neo.twig.input.InputService;
import javafx.scene.input.KeyCode;

public class InputAction {
    // Taking a page out of Minecraft source code
    public static final InputAction MOVE_LEFT = new InputAction(CompareMode.Or, KeyCode.A, KeyCode.LEFT);
    public static final InputAction MOVE_RIGHT = new InputAction(CompareMode.Or, KeyCode.D, KeyCode.RIGHT);
    public static final InputAction MOVE_DOWN = new InputAction(CompareMode.Or, KeyCode.S, KeyCode.DOWN);

    public static final InputAction ROTATE_LEFT = new InputAction(CompareMode.Or, KeyCode.K);
    public static final InputAction ROTATE_RIGHT = new InputAction(CompareMode.Or, KeyCode.L);

    public static final InputAction PAUSE = new InputAction(CompareMode.Or, KeyCode.ESCAPE);

    private static InputService inputService;
    private final CompareMode mode;
    private final KeyCode[] keys;

    private InputAction(CompareMode mode, KeyCode... keys) {
        this.mode = mode;
        this.keys = keys;
    }

    public static void initialiseActions() {
        inputService = Engine.getInputService();
    }

    public boolean wasActivatedThisFrame() {
        switch (mode) {
            case Or -> {
                for (KeyCode keyCode : keys) {
                    if (inputService.wasKeyPressed(keyCode))
                        return true;
                }

                return false;
            }

            case And -> {
                for (KeyCode keyCode : keys) {
                    if (!inputService.wasKeyPressed(keyCode))
                        return false;
                }

                return true;
            }

            default -> {
                return false;
            }
        }
    }

    public boolean wasDeactivatedThisFrame() {
        switch (mode) {
            case Or -> {
                for (KeyCode keyCode : keys) {
                    if (inputService.wasKeyReleased(keyCode))
                        return true;
                }

                return false;
            }

            case And -> {
                for (KeyCode keyCode : keys) {
                    if (!inputService.wasKeyReleased(keyCode))
                        return false;
                }

                return true;
            }

            default -> {
                return false;
            }
        }
    }

    public boolean isActivationHeld() {
        switch (mode) {
            case Or -> {
                for (KeyCode keyCode : keys) {
                    if (inputService.isKeyHeld(keyCode))
                        return true;
                }

                return false;
            }

            case And -> {
                for (KeyCode keyCode : keys) {
                    if (!inputService.isKeyHeld(keyCode))
                        return false;
                }

                return true;
            }

            default -> {
                return false;
            }
        }
    }

    public enum CompareMode {
        Or,
        And
    }
}
