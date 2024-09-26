package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;

public class OpenDayLeaderboardRegisterComponent extends FXComponent {
    DecimalFormat scoreFormat = new DecimalFormat("0000000000");
    private GameManager gameManager;
    private int currentScore;
    @ForceSerialize
    private URLResource titleScene;

    private Label scoreLabel;
    private TextField usernameTextField;

    @Override
    public void start() {
        super.start();

        setVisible(false);

        SceneService sceneService = Engine.getSceneService();
        gameManager = sceneService.getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        gameManager.getGameDidEndEvent().addHandler((ignored) -> {
            currentScore = gameManager.getCurrentScore();
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

        Label usernameLabel = new Label("Username");
        usernameTextField = new TextField();

        Button submitScoreButton = new Button("Submit Score");
        submitScoreButton.setOnAction(this::onSubmitScoreButton);

        Button skipButton = new Button("Skip");
        skipButton.setOnAction(this::onSkipButton);

        verticalContainer.getChildren().addAll(
                gameOverLabel,
                scoreLabel,

                usernameLabel,
                usernameTextField,

                submitScoreButton,
                skipButton
        );

        verticalContainer.setId("vertical-container");

        return root;
    }

    private void onSubmitScoreButton(ActionEvent event) {
        if (usernameTextField.getText().isEmpty()) {
            showInvalidUsernameMessage();
        } else {
            LeaderboardService.getInstance().uploadScore(currentScore, usernameTextField.getText());
            showThankYouForPlayingMessage();
        }
    }

    private void onSkipButton(ActionEvent event) {
        showThankYouForPlayingMessage();
    }

    private void showThankYouForPlayingMessage() {
        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "Thank You",
                        "Thank you for playing! Please consider joining the IO society!",
                        new MessageOption("Quit to Title Screen", (ignored) -> Engine.getSceneService().setScene(titleScene.get()))
                )
        );
    }

    private void showInvalidUsernameMessage() {
        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "Error",
                        "You need to set a valid username to upload your score.",
                        new MessageOption("Ok", null)
                )
        );
    }
}
