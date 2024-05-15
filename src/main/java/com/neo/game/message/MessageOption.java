package com.neo.game.message;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Represents an option a user can take on a given UI message
 *
 * @param text Button text
 * @param action Action to perform when the button is clicked (can be null)
 */
public record MessageOption(
   String text,
   EventHandler<ActionEvent> action
) {
    
}
