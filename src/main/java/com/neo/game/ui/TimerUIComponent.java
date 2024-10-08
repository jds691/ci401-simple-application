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

    private boolean hasSeenCritical = false;

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

        if (!hasSeenCritical && time <= 60000) {
            timeLabel.getStyleClass().add("critical");

            //TODO: Show Splatoon 1 minute left texture animated

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
