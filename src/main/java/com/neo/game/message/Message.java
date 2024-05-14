package com.neo.game.message;

public record Message(
        String title,
        String message,
        MessageOption... options
) {
}

