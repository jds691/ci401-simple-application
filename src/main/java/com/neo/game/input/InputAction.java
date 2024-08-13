package com.neo.game.input;

import com.neo.game.settings.KeyBindSettings;
import com.neo.twig.Engine;
import com.neo.twig.input.InputService;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the Engine's {@link InputService} code into reusable chunks that support a variable amount of keys
 */
public class InputAction {
    private static final Map<Input, InputAction> actionCache = new HashMap<>();

    private static InputService inputService;
    private final KeyCode[] keys;

    private InputAction(KeyCode... keys) {
        this.keys = keys;
    }

    /**
     * Gets (or creates) an input action pre-configured with the users settings in {@link KeyBindSettings}
     *
     * @param input Input to get the action for
     * @return Constructed action
     */
    public static InputAction get(Input input) {
        if (actionCache.containsKey(input))
            return actionCache.get(input);

        InputAction action = new InputAction(KeyBindSettings.getInstance().getKeysForInput(input));
        actionCache.put(input, action);

        return action;
    }

    public static void clearActionCache() {
        actionCache.clear();
    }

    /**
     * Gets the stores the relevant engine code and services for action construction
     */
    public static void initialiseActions() {
        inputService = Engine.getInputService();
    }

    /**
     * Maps to {@link InputService#wasKeyPressed(KeyCode)}
     *
     * @return If the action was "pressed" this frame
     */
    public boolean wasActivatedThisFrame() {
        for (KeyCode keyCode : keys) {
            if (inputService.wasKeyPressed(keyCode))
                return true;
        }

        return false;
    }

    /**
     * Maps to {@link InputService#wasKeyReleased(KeyCode)}
     *
     * @return If the action was "released" this frame
     */
    public boolean wasDeactivatedThisFrame() {
        for (KeyCode keyCode : keys) {
            if (inputService.wasKeyReleased(keyCode))
                return true;
        }

        return false;
    }

    /**
     * Maps to {@link InputService#isKeyHeld(KeyCode)}
     *
     * @return If the action is currently being held
     */
    public boolean isActivationHeld() {
        for (KeyCode keyCode : keys) {
            if (inputService.isKeyHeld(keyCode))
                return true;
        }

        return false;
    }
}
