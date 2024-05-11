package com.neo.game.input;

import com.neo.game.settings.KeyBindSettings;
import com.neo.twig.Engine;
import com.neo.twig.input.InputService;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class InputAction {
    private static final Map<Input, InputAction> actionCache = new HashMap<>();

    private static InputService inputService;
    private final KeyCode[] keys;

    private InputAction(KeyCode... keys) {
        this.keys = keys;
    }

    public static InputAction get(Input input) {
        if (actionCache.containsKey(input))
            return actionCache.get(input);

        InputAction action = new InputAction(KeyBindSettings.getInstance().getKeysForInput(input));
        actionCache.put(input, action);

        return action;
    }

    public static void initialiseActions() {
        inputService = Engine.getInputService();
    }

    public boolean wasActivatedThisFrame() {
        for (KeyCode keyCode : keys) {
            if (inputService.wasKeyPressed(keyCode))
                return true;
        }

        return false;
    }

    public boolean wasDeactivatedThisFrame() {
        for (KeyCode keyCode : keys) {
            if (inputService.wasKeyReleased(keyCode))
                return true;
        }

        return false;
    }

    public boolean isActivationHeld() {
        for (KeyCode keyCode : keys) {
            if (inputService.isKeyHeld(keyCode))
                return true;
        }

        return false;
    }
}
