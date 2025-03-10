package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.game.message.MessageServiceComponent;
import com.neo.game.message.SystemMessage;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.SceneService;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;

public class GameOverUIComponent extends FXComponent {
    private GameManager gameManager;

    private int currentScore;
    DecimalFormat scoreFormat = new DecimalFormat("0000000000");

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

        gameManager.getGameDidEndEvent().addHandler((ignored) -> {
            scoreLabel.setText("Final Score: " + scoreFormat.format(gameManager.getCurrentScore()));
            setVisible(true);
        });
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        VBox verticalContainer = new VBox();
        verticalContainer.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(root, Pos.CENTER);
        root.setCenter(verticalContainer);

        Label gameOverLabel = new Label("game over");
        gameOverLabel.getStyleClass().add("title-label");

        scoreLabel = new Label("Final Score: 0000000000");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(this::onRestartButton);

        Button quitToTitleButton = new Button("Quit to Title Screen");
        quitToTitleButton.setOnAction(this::onQuitToTitleButton);

        Button quitGameButton = new Button("Quit to Desktop");
        quitGameButton.setOnAction(this::onQuitGameButton);

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
        MessageServiceComponent.getInstance().addToQueue(SystemMessage.QUIT_TO_DESKTOP);
    }
}
