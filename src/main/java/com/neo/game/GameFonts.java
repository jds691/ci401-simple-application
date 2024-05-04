package com.neo.game;

import javafx.scene.text.Font;

import java.io.IOException;

public class GameFonts {
    public static final Font SIDE_ORDER_TITLE;
    public static final Font SIDE_ORDER_BODY;

    static {
        try {
            SIDE_ORDER_TITLE = Font.loadFont(GameFonts.class.getResource("SpAlterna-Regular.otf").openStream(), 24);
            SIDE_ORDER_BODY = Font.loadFont(GameFonts.class.getResource("FOT-RowdyStd-EB.otf").openStream(), 12);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
