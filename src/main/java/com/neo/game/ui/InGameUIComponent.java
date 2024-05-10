package com.neo.game.ui;

import com.neo.twig.ui.FXComponent;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class InGameUIComponent extends FXComponent {
    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("in-game-root");

        return root;
    }
}
