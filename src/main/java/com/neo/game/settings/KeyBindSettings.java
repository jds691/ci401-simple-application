package com.neo.game.settings;

import com.neo.game.input.Input;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigProperty;
import javafx.scene.input.KeyCode;

import java.security.InvalidParameterException;

/**
 * A configuration class that is serialized which allows the user to change the keybinds for each {@link com.neo.game.input.InputAction}
 */
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

    /**
     * Gets (or constructs) the singleton instance of this object.
     *
     * @return Singleton instance
     */
    public static KeyBindSettings getInstance() {
        if (instance == null) {
            instance = new KeyBindSettings();
        }

        return instance;
    }

    /**
     * Returns the keybinds the user has register to the input
     *
     * @param input The input to find keybinds for
     * @return The keybinds associated with the input
     */
    public KeyCode getKeysForInput(Input input) {
        return switch (input) {
            case MOVE_LEFT -> moveLeft;
            case MOVE_RIGHT -> moveRight;
            case MOVE_DOWN -> moveDown;

            case ROTATE_LEFT -> rotateLeft;
            case ROTATE_RIGHT -> rotateRight;

            case PAUSE -> pause;
        };
    }

    /**
     * Gets a settings category that can be configured by {@link SettingsUIComponent}
     *
     * @return Preconfigured category
     */
    public SettingCategory getSettingsCategory() {
        SettingCategory category = new SettingCategory();
        category.setName("Keybindings");

        SettingCategory gameplayCategory = new SettingCategory();
        gameplayCategory.setName("Gameplay");

        KeyCodeSetting moveLeftSetting = new KeyCodeSetting();
        moveLeftSetting.setName("Move Left");
        moveLeftSetting.setDefaultValue(KeyCode.A);
        moveLeftSetting.setValueSetter(new KeyCodeSetter(Input.MOVE_LEFT));
        moveLeftSetting.setValueGetter(new KeyCodeGetter(Input.MOVE_LEFT));

        KeyCodeSetting moveRightSetting = new KeyCodeSetting();
        moveRightSetting.setName("Move Right");
        moveRightSetting.setDefaultValue(KeyCode.D);
        moveRightSetting.setValueSetter(new KeyCodeSetter(Input.MOVE_RIGHT));
        moveRightSetting.setValueGetter(new KeyCodeGetter(Input.MOVE_RIGHT));

        KeyCodeSetting moveDownSetting = new KeyCodeSetting();
        moveDownSetting.setName("Move Down");
        moveDownSetting.setDefaultValue(KeyCode.S);
        moveDownSetting.setValueSetter(new KeyCodeSetter(Input.MOVE_DOWN));
        moveDownSetting.setValueGetter(new KeyCodeGetter(Input.MOVE_DOWN));

        KeyCodeSetting rotateLeftSetting = new KeyCodeSetting();
        rotateLeftSetting.setName("Rotate Left");
        rotateLeftSetting.setDefaultValue(KeyCode.K);
        rotateLeftSetting.setValueSetter(new KeyCodeSetter(Input.ROTATE_LEFT));
        rotateLeftSetting.setValueGetter(new KeyCodeGetter(Input.ROTATE_LEFT));

        KeyCodeSetting rotateRightSetting = new KeyCodeSetting();
        rotateRightSetting.setName("Rotate Right");
        rotateRightSetting.setDefaultValue(KeyCode.L);
        rotateRightSetting.setValueSetter(new KeyCodeSetter(Input.ROTATE_RIGHT));
        rotateRightSetting.setValueGetter(new KeyCodeGetter(Input.ROTATE_RIGHT));

        gameplayCategory.addChildren(moveLeftSetting, moveRightSetting, moveDownSetting, rotateLeftSetting, rotateRightSetting);

        SettingCategory systemCategory = new SettingCategory();
        systemCategory.setName("System");

        KeyCodeSetting pauseSetting = new KeyCodeSetting();
        pauseSetting.setName("Pause");
        pauseSetting.setDefaultValue(KeyCode.ESCAPE);
        pauseSetting.setValueSetter(new KeyCodeSetter(Input.PAUSE));
        pauseSetting.setValueGetter(new KeyCodeGetter(Input.PAUSE));

        systemCategory.addChildren(pauseSetting);

        category.addChildren(gameplayCategory, systemCategory);
        category.setSaveAction(this::save);

        return category;
    }

    /**
     * Saves the current settings for the user
     */
    public void save() {
        ConfigManager.saveConfig(this);
    }

    private class KeyCodeSetter implements DynamicSetter<KeyCode> {
        private Input input;

        public KeyCodeSetter(Input input) {
            this.input = input;
        }

        @Override
        public void set(KeyCode value) {
            switch (input) {
                case MOVE_LEFT -> moveLeft = value;
                case MOVE_RIGHT -> moveRight = value;
                case MOVE_DOWN -> moveDown = value;

                case ROTATE_LEFT -> rotateLeft = value;
                case ROTATE_RIGHT -> rotateRight = value;

                case PAUSE -> pause = value;
            }
        }
    }

    private class KeyCodeGetter implements DynamicGetter<KeyCode> {
        private Input input;

        public KeyCodeGetter(Input input) {
            this.input = input;
        }

        @Override
        public KeyCode get() {
            return getKeysForInput(input);
        }
    }
}
