package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.ui.FXComponent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.time.Duration;

public class TimerUIComponent extends FXComponent {
    @ForceSerialize
    private double time;
    private Label timeLabel;

    private GameManager gameManager;
    private boolean gamePaused = false;

    private static final float CRITICAL_TIME = 61000;
    private boolean hasSeenCritical = false;
    private boolean musicTransistionQueued = false;

    @Override
    public void start() {
        super.start();

        gameManager = Engine.getSceneService()
                .getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        gameManager
                .getPauseDidChangeEvent()
                .addHandler((paused) -> {
                    gamePaused = paused;
                });

        gameManager
                .getGameDidEndEvent()
                .addHandler((reason) -> {
                    gamePaused = true;
                    if (reason == GameManager.EndReason.TIME) {
                        timeLabel.setText("00:00");
                    }
                });
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (gamePaused)
            return;

        time -= deltaTime;

        if (time <= 0) {
            gamePaused = true;
            gameManager.signalGameEnd(GameManager.EndReason.TIME);
        }

        Duration duration = Duration.ofMillis((long) time);
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));

        if (!musicTransistionQueued && time <= CRITICAL_TIME + 3000) {
            gameManager.beginMusicTransition();
            musicTransistionQueued = true;
        }

        if (!hasSeenCritical && time <= CRITICAL_TIME) {
            timeLabel.getStyleClass().add("critical");

            Engine.getSceneService()
                    .getActiveScene()
                    .findRootNode("Critical Overlay")
                    .getComponent(MinuteRemainingOverlayComponent.class)
                    .setEnabled(true);

            hasSeenCritical = true;
        }
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();

        Duration duration = Duration.ofMillis((long) time);
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        timeLabel = new Label(String.format("%02d:%02d", minutes, seconds));
        timeLabel.getStyleClass().add("time-label");

        timeLabel.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(timeLabel, Pos.CENTER);
        root.setTop(timeLabel);

        return root;
    }
}
