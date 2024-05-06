package com.neo.game.ui;

import com.neo.game.GameFonts;
import com.neo.game.GameManager;
import com.neo.game.GameStyles;
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
        root.setStyle(
                """
                        -fx-background-color: rgba(0, 0, 0, 0.4);
                        """
        );

        VBox verticalContainer = new VBox();
        verticalContainer.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(root, Pos.CENTER);
        root.setCenter(verticalContainer);

        Label gameOverLabel = new Label("game over");
        gameOverLabel.setFont(GameFonts.SIDE_ORDER_TITLE);
        gameOverLabel.setTextFill(Color.WHITE);

        Label scoreLabel = new Label();
        scoreLabel.setFont(GameFonts.SIDE_ORDER_BODY);
        scoreLabel.setTextFill(Color.WHITE);

        Button restartButton = new Button("Restart");
        restartButton.setFont(GameFonts.SIDE_ORDER_BODY);
        restartButton.setTextFill(Color.WHITE);
        restartButton.setPadding(new Insets(15));
        restartButton.setOnAction(this::onRestartButton);
        restartButton.setStyle(GameStyles.BUTTON);
        restartButton.setMaxWidth(Double.MAX_VALUE);

        Button quitToTitleButton = new Button("Quit to Title Screen");
        quitToTitleButton.setFont(GameFonts.SIDE_ORDER_BODY);
        quitToTitleButton.setTextFill(Color.WHITE);
        quitToTitleButton.setPadding(new Insets(15));
        quitToTitleButton.setOnAction(this::onQuitToTitleButton);
        quitToTitleButton.setStyle(GameStyles.BUTTON);
        quitToTitleButton.setMaxWidth(Double.MAX_VALUE);

        Button quitGameButton = new Button("Quit to Desktop");
        quitGameButton.setFont(GameFonts.SIDE_ORDER_BODY);
        quitGameButton.setTextFill(Color.WHITE);
        quitGameButton.setPadding(new Insets(15));
        quitGameButton.setOnAction(this::onQuitGameButton);
        quitGameButton.setStyle(GameStyles.BUTTON);
        quitGameButton.setMaxWidth(Double.MAX_VALUE);

        verticalContainer.getChildren().add(gameOverLabel);
        verticalContainer.getChildren().add(scoreLabel);
        verticalContainer.getChildren().add(restartButton);
        verticalContainer.getChildren().add(quitToTitleButton);
        verticalContainer.getChildren().add(quitGameButton);

        verticalContainer.setPadding(new Insets(50));
        verticalContainer.setSpacing(16);

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
