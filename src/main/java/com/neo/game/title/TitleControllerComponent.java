package com.neo.game.title;

import com.neo.game.leaderboard.LeaderboardScore;
import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.leaderboard.LeaderboardUIComponent;
import com.neo.game.message.MessageServiceComponent;
import com.neo.game.message.SystemMessage;
import com.neo.game.settings.SettingsUIComponent;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.Scene;
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

    @ForceSerialize
    private double iconSize;

    private LeaderboardUIComponent leaderboardUI;
    private SettingsUIComponent settingsUI;

    @Override
    public void start() {
        super.start();

        Scene activeScene = Engine.getSceneService().getActiveScene();
        leaderboardUI = activeScene.findRootNode("Leaderboard UI").getComponent(LeaderboardUIComponent.class);
        settingsUI = activeScene.findRootNode("Settings UI").getComponent(SettingsUIComponent.class);
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        BorderPane bottom = new BorderPane();
        bottom.setId("ui-bottom");

        BorderPane top = new BorderPane();
        top.setId("ui-top");

        VBox scoreInfo = new VBox();
        scoreInfo.setAlignment(Pos.CENTER);
        Label topScoreTitle = new Label("Top Score:");
        topScoreTitle.getStyleClass().add("version-label");

        LeaderboardScore[] scores = LeaderboardService.getInstance().getAllScores();
        Label topScoreInfo;

        if (scores.length > 0) {
            LeaderboardScore topScore = scores[0];
            topScoreInfo = new Label(String.format("%s - %s", topScore.getUserName(), topScore.getScore()));
        } else {
            topScoreInfo = new Label("None");
        }

        topScoreInfo.getStyleClass().add("version-label");

        scoreInfo.getChildren().addAll(
                topScoreTitle,
                topScoreInfo
        );

        VBox buttonContainer = new VBox();

        Button playButton = new Button("Play");
        playButton.setOnAction(this::onPlayButtonAction);

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(this::onSettingsButtonAction);

        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setOnAction(this::onLeaderboardButtonAction);

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(this::onQuitButtonAction);

        Label gameVersion = new Label("Version: " + Engine.getConfig().appConfig().version);
        gameVersion.getStyleClass().add("version-label");
        BorderPane.setAlignment(gameVersion, Pos.BOTTOM_LEFT);

        buttonContainer.getChildren().add(playButton);
        buttonContainer.getChildren().add(leaderboardButton);
        buttonContainer.getChildren().add(settingsButton);
        //TODO: Ask for confirmation before quitting
        buttonContainer.getChildren().add(quitButton);
        buttonContainer.setId("button-container");

        VBox titleCenter = new VBox();
        titleCenter.setId("content-center");

        Label titleLabel = new Label("tetris");
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setPadding(new Insets(0, 0, iconSize, 0));

        titleCenter.getChildren().add(titleLabel);

        bottom.setLeft(gameVersion);
        bottom.setRight(buttonContainer);
        top.setRight(scoreInfo);
        root.setBottom(bottom);
        root.setTop(top);
        BorderPane.setAlignment(titleCenter, Pos.CENTER);
        root.setCenter(titleCenter);

        return root;
    }

    private void onPlayButtonAction(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(gameScene.get());
    }

    private void onLeaderboardButtonAction(ActionEvent actionEvent) {
        showLeaderboard();
    }

    private void showLeaderboard() {
        leaderboardUI.setVisible(true);
    }

    private void onSettingsButtonAction(ActionEvent actionEvent) {
        settingsUI.setVisible(true);
    }

    private void onQuitButtonAction(ActionEvent actionEvent) {
        MessageServiceComponent.getInstance().addToQueue(SystemMessage.QUIT_TO_DESKTOP);
    }
}
