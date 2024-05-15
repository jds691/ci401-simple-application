package com.neo.game.title;

import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.leaderboard.LeaderboardSettings;
import com.neo.game.leaderboard.LeaderboardUIComponent;
import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.game.message.SystemMessage;
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

    @ForceSerialize
    private double iconSize;

    private LeaderboardUIComponent leaderboardUI;

    @Override
    public void start() {
        super.start();

        leaderboardUI = Engine.getSceneService().getActiveScene().findRootNode("Leaderboard UI").getComponent(LeaderboardUIComponent.class);
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        BorderPane bottom = new BorderPane();
        bottom.setId("ui-bottom");

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
        root.setBottom(bottom);
        BorderPane.setAlignment(titleCenter, Pos.CENTER);
        root.setCenter(titleCenter);

        return root;
    }

    private void onPlayButtonAction(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(gameScene.get());
    }

    private void onLeaderboardButtonAction(ActionEvent actionEvent) {
        if (!LeaderboardService.getInstance().getSettings().hasSeenInitialMessage()) {
            LeaderboardService.getInstance().playIntro(false);
        } else if (!LeaderboardService.getInstance().getSettings().isEnabled()) {
            MessageServiceComponent.getInstance().addToQueue(
                    new Message("Notice",
                            "The leaderboard has not been enabled. Would you like to enable it?",
                            new MessageOption(
                                    "Yes",
                                    this::handleEnableLeaderboard
                            ),
                            new MessageOption(
                                    "No",
                                    null
                            )
                    )
            );
        } else {
            showLeaderboard();
        }
    }

    private void handleEnableLeaderboard(ActionEvent event) {
        LeaderboardService.getInstance().getSettings().setIsEnabled(true);
        LeaderboardService.getInstance().saveSettings();

        MessageServiceComponent.getInstance().addToQueue(
                new Message("Notice",
                        "The leaderboard has been enabled.",
                        new MessageOption(
                                "OK",
                                this::onLeaderboardButtonAction
                        )
                )
        );
    }

    private void showLeaderboard() {
        leaderboardUI.setVisible(true);
    }

    private void onSettingsButtonAction(ActionEvent actionEvent) {
    }

    private void onQuitButtonAction(ActionEvent actionEvent) {
        MessageServiceComponent.getInstance().addToQueue(SystemMessage.QUIT_TO_DESKTOP);
    }
}
