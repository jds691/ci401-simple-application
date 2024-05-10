package com.neo.game.title;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.URLResource;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TitleControllerComponent extends FXComponent {
    @ForceSerialize
    private URLResource gameScene;

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        BorderPane bottom = new BorderPane();
        bottom.setId("ui-bottom");

        VBox buttonContainer = new VBox();

        Button playButton = new Button("Play");
        playButton.setOnAction(this::onPlayButtonAction);
        playButton.getStyleClass().add("button");

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(this::onSettingsButtonAction);
        settingsButton.getStyleClass().add("button");

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(this::onQuitButtonAction);
        quitButton.getStyleClass().add("button");

        Label gameVersion = new Label("Version: " + Engine.getConfig().appConfig().version);
        gameVersion.getStyleClass().addAll("label", "version-label");
        BorderPane.setAlignment(gameVersion, Pos.BOTTOM_LEFT);

        buttonContainer.getChildren().add(playButton);
        //buttonContainer.getChildren().add(settingsButton);
        //TODO: Ask for confirmation before quitting
        buttonContainer.getChildren().add(quitButton);
        buttonContainer.setId("button-container");

        bottom.setLeft(gameVersion);
        bottom.setRight(buttonContainer);
        root.setBottom(bottom);

        return root;
    }

    private void onPlayButtonAction(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(gameScene.get());
    }

    private void onSettingsButtonAction(ActionEvent actionEvent) {
    }

    private void onQuitButtonAction(ActionEvent actionEvent) {
        Engine.quit();
    }
}
