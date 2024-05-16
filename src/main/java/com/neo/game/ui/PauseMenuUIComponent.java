package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.game.audio.MusicComponent;
import com.neo.game.audio.SFXPlayer;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PauseMenuUIComponent extends FXComponent {
    private final SFXPlayer pauseOpenSfx = new SFXPlayer("SFX_Pause_Open");
    private final SFXPlayer pauseCloseSfx = new SFXPlayer("SFX_Pause_Close");
    private final SFXPlayer resumeSfx = new SFXPlayer("SFX_Resume");
    private final SFXPlayer quitSfx = new SFXPlayer("SFX_Quit");
    private GameManager gameManager;
    private boolean isStarting = true;

    private MusicComponent gameMusic;

    @ForceSerialize
    private URLResource gameScene;

    @ForceSerialize
    private URLResource titleScene;

    @Override
    public void start() {
        super.start();

        setVisible(false);

        SceneService sceneService = Engine.getSceneService();
        gameManager = sceneService.getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        gameManager.getPauseDidChangeEvent().addHandler(this::setVisible);

        gameMusic = sceneService.getActiveScene()
                .findRootNode("Game Music")
                .getComponent(MusicComponent.class);

        isStarting = false;
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        VBox verticalContainer = new VBox();
        verticalContainer.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(root, Pos.CENTER);
        root.setCenter(verticalContainer);

        Label pauseLabel = new Label("paused");
        pauseLabel.getStyleClass().add("title-label");

        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(this::onResumeButton);

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(this::onRestartButton);

        Button quitToTitleButton = new Button("Quit to Title Screen");
        quitToTitleButton.setOnAction(this::onQuitToTitleButton);

        verticalContainer.getChildren().add(pauseLabel);
        verticalContainer.getChildren().add(resumeButton);
        verticalContainer.getChildren().add(restartButton);
        verticalContainer.getChildren().add(quitToTitleButton);

        verticalContainer.setId("vertical-container");

        return root;
    }

    private void onResumeButton(ActionEvent actionEvent) {
        gameManager.setIsPaused(false);
        resumeSfx.play();
    }

    private void onRestartButton(ActionEvent actionEvent) {
        MessageServiceComponent.getInstance().addToQueue(
                new Message("Notice",
                        "Are you sure you want to restart?",
                        new MessageOption(
                                "Yes",
                                (ignored) -> Engine.getSceneService().setScene(gameScene.get())
                        ),
                        new MessageOption(
                                "No",
                                null
                        )
                )
        );
    }

    private void onQuitToTitleButton(ActionEvent actionEvent) {
        boolean leaderboardEnabled = LeaderboardService.getInstance().getSettings().isEnabled();
        MessageServiceComponent.getInstance().addToQueue(
                new Message("Notice",
                        leaderboardEnabled ?
                                "Are you sure you want to quit the current game? Your score will not be saved." :
                                "Are you sure you want to quit the current game?",
                        new MessageOption(
                                "Yes",
                                (ignored) -> Engine.getSceneService().setScene(titleScene.get())
                        ),
                        new MessageOption(
                                "No",
                                null
                        )
                )
        );
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (isStarting)
            return;

        // Used to automatically handle SFX playback
        if (visible) {
            gameMusic.pause();
            pauseOpenSfx.play();
        } else {
            gameMusic.play();
            pauseCloseSfx.play();
        }
    }
}
