package com.neo.game.settings;

import com.neo.game.input.Input;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigProperty;
import javafx.scene.input.KeyCode;

@Config(name = "keybinds")
public class KeyBindSettings {
    private static KeyBindSettings instance;

    @ForceSerialize
    @ConfigProperty(section = "Gameplay")
    private KeyCode moveLeft;
    @ForceSerialize
    @ConfigProperty(section = "Gameplay")
    private KeyCode moveRight;
    @ForceSerialize
    @ConfigProperty(section = "Gameplay")
    private KeyCode moveDown;

    @ForceSerialize
    @ConfigProperty(section = "Gameplay")
    private KeyCode rotateLeft;
    @ForceSerialize
    @ConfigProperty(section = "Gameplay")
    private KeyCode rotateRight;

    @ForceSerialize
    @ConfigProperty(section = "System")
    private KeyCode pause;

    private KeyBindSettings() {
        moveLeft = KeyCode.A;
        moveRight = KeyCode.D;
        moveDown = KeyCode.S;

        rotateLeft = KeyCode.K;
        rotateRight = KeyCode.L;

        pause = KeyCode.ESCAPE;
    }

    public static KeyBindSettings getInstance() {
        if (instance == null) {
            instance = new KeyBindSettings();
        }

        return instance;
    }

    public KeyCode getKeysForInput(Input input) {
        return switch (input) {
            case MOVE_LEFT -> moveLeft;
            case MOVE_RIGHT -> moveRight;
            case MOVE_DOWN -> moveDown;

            case ROTATE_LEFT -> rotateLeft;
            case ROTATE_RIGHT -> rotateRight;

            case PAUSE -> pause;

            case MAX -> KeyCode.ALL_CANDIDATES;
        };
    }
}
