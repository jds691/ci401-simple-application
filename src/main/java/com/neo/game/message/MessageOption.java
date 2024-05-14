package com.neo.game.message;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public record MessageOption(
   String text,
   EventHandler<ActionEvent> action
) {
    
}
