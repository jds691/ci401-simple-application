package com.neo.game.message;

/**
 * Contains the data to display a UI message to the user
 *
 * @param title Message UI title
 * @param message Message UI text
 * @param options The available options for the user to pick (Represented as buttons)
 */
public record Message(
        String title,
        String message,
        MessageOption... options
) {
}

