package com.neo.game.title;

import com.neo.twig.Engine;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TitleControllerComponent extends FXComponent {
    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: transparent;");

        BorderPane bottom = new BorderPane();
        bottom.setStyle("-fx-background-color: transparent;");

        VBox buttonContainer = new VBox();

        Button playButton = new Button("Play");
        playButton.setOnAction(this::onPlayButtonAction);

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(this::onSettingsButtonAction);

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(this::onQuitButtonAction);

        Label gameVersion = new Label("Version: " + Engine.getConfig().appConfig().version);
        gameVersion.setTextFill(Color.WHITE);

        buttonContainer.getChildren().add(playButton);
        buttonContainer.getChildren().add(settingsButton);
        //TODO: Ask for confirmation before quitting
        buttonContainer.getChildren().add(quitButton);

        bottom.setLeft(gameVersion);
        bottom.setRight(buttonContainer);
        root.setBottom(bottom);

        return root;
    }

    private void onPlayButtonAction(ActionEvent actionEvent) {
    }

    private void onSettingsButtonAction(ActionEvent actionEvent) {
    }

    private void onQuitButtonAction(ActionEvent actionEvent) {
        Engine.quit();
    }
}
