package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.game.audio.SFXPlayer;
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
import javafx.scene.paint.Color;

public class PauseMenuUIComponent extends FXComponent {
    private final SFXPlayer pauseOpenSfx = new SFXPlayer("SFX_Pause_Open");
    /*private final SFXPlayer pauseLoopSfx;*/
    private final SFXPlayer pauseCloseSfx = new SFXPlayer("SFX_Pause_Close");
    private final SFXPlayer resumeSfx = new SFXPlayer("SFX_Resume");
    private final SFXPlayer quitSfx = new SFXPlayer("SFX_Quit");
    private GameManager gameManager;
    private boolean isStarting = true;

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

        isStarting = false;
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("pause-root");

        VBox verticalContainer = new VBox();
        verticalContainer.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(root, Pos.CENTER);
        root.setCenter(verticalContainer);

        Label pauseLabel = new Label("paused");
        pauseLabel.getStyleClass().add("title-label");

        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().addAll("button", "pause-button");
        resumeButton.setOnAction(this::onResumeButton);

        Button restartButton = new Button("Restart");
        restartButton.getStyleClass().addAll("button", "pause-button");
        restartButton.setOnAction(this::onRestartButton);

        Button quitToTitleButton = new Button("Quit to Title Screen");
        quitToTitleButton.getStyleClass().addAll("button", "pause-button");
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
        Engine.getSceneService().setScene(gameScene.get());
    }

    private void onQuitToTitleButton(ActionEvent actionEvent) {
        Engine.getSceneService().setScene(titleScene.get());
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (isStarting)
            return;

        if (visible) {
            pauseOpenSfx.play();
        } else {
            pauseCloseSfx.play();
        }
    }
}
