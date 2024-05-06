package com.neo.game.title;

import com.neo.game.GameFonts;
import com.neo.game.GameStyles;
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
import javafx.scene.paint.Color;

public class TitleControllerComponent extends FXComponent {
    @ForceSerialize
    private URLResource gameScene;

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: transparent;");

        BorderPane bottom = new BorderPane();
        bottom.setStyle("-fx-background-color: transparent;");

        VBox buttonContainer = new VBox();

        Button playButton = new Button("Play");
        playButton.setOnAction(this::onPlayButtonAction);
        playButton.setFont(GameFonts.SIDE_ORDER_BODY);
        playButton.setTextFill(Color.WHITE);
        playButton.setPadding(new Insets(15));
        playButton.setStyle(GameStyles.BUTTON);
        playButton.setMaxWidth(150);

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(this::onSettingsButtonAction);
        settingsButton.setFont(GameFonts.SIDE_ORDER_BODY);
        settingsButton.setTextFill(Color.WHITE);
        settingsButton.setPadding(new Insets(15));
        settingsButton.setStyle(GameStyles.BUTTON);
        settingsButton.setMaxWidth(150);

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(this::onQuitButtonAction);
        quitButton.setFont(GameFonts.SIDE_ORDER_BODY);
        quitButton.setTextFill(Color.WHITE);
        quitButton.setPadding(new Insets(15));
        quitButton.setStyle(GameStyles.BUTTON);
        quitButton.setMaxWidth(150);

        Label gameVersion = new Label("Version: " + Engine.getConfig().appConfig().version);
        gameVersion.setFont(GameFonts.SIDE_ORDER_BODY);
        BorderPane.setAlignment(gameVersion, Pos.BOTTOM_LEFT);

        buttonContainer.getChildren().add(playButton);
        //buttonContainer.getChildren().add(settingsButton);
        //TODO: Ask for confirmation before quitting
        buttonContainer.getChildren().add(quitButton);
        buttonContainer.setSpacing(8);

        bottom.setLeft(gameVersion);
        bottom.setRight(buttonContainer);
        bottom.setPadding(new Insets(16));
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
