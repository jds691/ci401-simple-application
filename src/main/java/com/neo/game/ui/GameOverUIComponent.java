package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.SceneService;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameOverUIComponent extends FXComponent {
    private GameManager gameManager;

    private int currentScore;

    @ForceSerialize
    private URLResource gameScene;

    @ForceSerialize
    private URLResource titleScene;

    private Label scoreLabel;

    @Override
    public void start() {
        super.start();

        setVisible(false);

        SceneService sceneService = Engine.getSceneService();
        gameManager = sceneService.getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        gameManager.getCurrentScoreDidChangeEvent().addHandler((deltaScore) -> {
            currentScore += deltaScore;
            scoreLabel.setText(String.format("Final Score: %10d", currentScore));
        });
        gameManager.getGameDidEndEvent().addHandler((ignored) -> {
            setVisible(true);
        });
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("game-over-root");

        VBox verticalContainer = new VBox();
        verticalContainer.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(root, Pos.CENTER);
        root.setCenter(verticalContainer);

        Label gameOverLabel = new Label("game over");
        gameOverLabel.getStyleClass().add("title-label");

        scoreLabel = new Label("Final Score: 0000000000");
        scoreLabel.getStyleClass().add("label");

        gameOverLabel.getStyleClass().add("label");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(this::onRestartButton);
        restartButton.getStyleClass().addAll("button", "game-over-button");

        Button quitToTitleButton = new Button("Quit to Title Screen");
        quitToTitleButton.setOnAction(this::onQuitToTitleButton);
        quitToTitleButton.getStyleClass().addAll("button", "game-over-button");

        Button quitGameButton = new Button("Quit to Desktop");
        quitGameButton.setOnAction(this::onQuitGameButton);
        quitGameButton.getStyleClass().addAll("button", "game-over-button");

        verticalContainer.getChildren().add(gameOverLabel);
        verticalContainer.getChildren().add(scoreLabel);
        verticalContainer.getChildren().add(restartButton);
        verticalContainer.getChildren().add(quitToTitleButton);
        verticalContainer.getChildren().add(quitGameButton);

        verticalContainer.setId("vertical-container");

        return root;
    }

    private void onRestartButton(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(gameScene.get());
    }

    private void onQuitToTitleButton(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(titleScene.get());
    }

    private void onQuitGameButton(ActionEvent actionEvent) {
        Engine.quit();
    }
}
