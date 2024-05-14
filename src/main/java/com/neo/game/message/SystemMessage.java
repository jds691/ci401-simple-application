package com.neo.game.message;

import com.neo.twig.Engine;

public class SystemMessage {
    public static final Message QUIT_TO_DESKTOP = new Message(
            "Notice",
            "Are you sure you want to quit to the desktop?",
            new MessageOption(
                    "Yes",
                    (event) -> Engine.quit()
            ),
            new MessageOption(
                    "No",
                    null
            )
    );
}
