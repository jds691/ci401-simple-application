package com.neo.game.ui;

import com.neo.game.Block;
import com.neo.game.BlockFormation;
import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.ImageResource;
import com.neo.twig.ui.FXComponent;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class InGameUIComponent extends FXComponent {
    private GameManager gameManager;

    DecimalFormat scoreFormat = new DecimalFormat("0000000000");
    private int currentScore;
    private int currentScoreFinal;
    private int currentDeltaScore;

    private int currentScoreAnimated;
    private int currentDeltaScoreAnimated;

    private Label upNextLabel;
    private Label totalScoreLabel;
    private Label deltaScoreLabel;
    private VBox blockQueue;

    @ForceSerialize
    private double thumbnailSize;

    @ForceSerialize
    private ImageResource[] blockThumbnails;

    @ForceSerialize
    private double textCountDuration;
    private float currentTextCounter;

    private boolean shouldAdjustDeltaLabel;

    private Timeline deltaFadeInAnimation;
    private Timeline deltaFadeOutAnimation;

    @Override
    public void start() {
        super.start();

        gameManager = Engine.getSceneService().getActiveScene().findRootNode("Game Context").getComponent(GameManager.class);
        gameManager.getBlockQueueDidChangeEvent().addHandler(this::handleBlockQueueChange);
        gameManager.getCurrentScoreDidChangeEvent().addHandler(this::handleScoreChange);

        //Once off initialisation
        ArrayDeque<BlockFormation> blocks = gameManager.getBlockQueue();
        ArrayList<Block.Color> blockQueueColors = new ArrayList<>(blocks.size());
        for (BlockFormation formation : blocks) {
            blockQueueColors.add(formation.getColor());
        }
        handleBlockQueueChange(blockQueueColors);
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        blockQueue = new VBox();
        blockQueue.setId("block-queue");
        blockQueue.getStyleClass().add("faded-background");

        upNextLabel = new Label("next");
        upNextLabel.setId("up-next");
        blockQueue.getChildren().add(upNextLabel);

        root.setRight(blockQueue);

        BorderPane scoreInfoAlignment = new BorderPane();
        VBox scoreInfo = new VBox();
        scoreInfo.setId("score-info");

        totalScoreLabel = new Label("Score: 0000000000");
        totalScoreLabel.getStyleClass().add("label");
        deltaScoreLabel = new Label("+000");
        deltaScoreLabel.getStyleClass().add("label");
        deltaScoreLabel.setId("delta-score-label");

        deltaFadeInAnimation = new Timeline();
        deltaFadeInAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(deltaScoreLabel.scaleXProperty(), 0),
                        new KeyValue(deltaScoreLabel.scaleYProperty(), 0),
                        new KeyValue(deltaScoreLabel.opacityProperty(), 1)
                ),
                new KeyFrame(new Duration(100),
                        new KeyValue(deltaScoreLabel.scaleXProperty(), 1),
                        new KeyValue(deltaScoreLabel.scaleYProperty(), 1)
                )
        );
        deltaFadeInAnimation.setOnFinished(this::handleFadeInFinish);
        deltaScoreLabel.setScaleX(0);
        deltaScoreLabel.setScaleY(0);

        deltaFadeOutAnimation = new Timeline();
        deltaFadeOutAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(deltaScoreLabel.opacityProperty(), 1)
                ),
                new KeyFrame(new Duration(500),
                        new KeyValue(deltaScoreLabel.translateYProperty(), 0),
                        new KeyValue(deltaScoreLabel.scaleXProperty(), 0),
                        new KeyValue(deltaScoreLabel.scaleYProperty(), 0)
                )
        );

        scoreInfo.getChildren().add(totalScoreLabel);
        scoreInfo.getChildren().add(deltaScoreLabel);

        scoreInfoAlignment.setTop(scoreInfo);
        root.setLeft(scoreInfoAlignment);

        return root;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (shouldAdjustDeltaLabel) {

            int start = currentScore;
            float progress = (float) (currentTextCounter / textCountDuration);

            currentTextCounter += deltaTime;

            currentScoreAnimated = Interpolator.LINEAR.interpolate(start, currentScoreFinal, progress);
            totalScoreLabel.setText("Score: " + scoreFormat.format(currentScoreAnimated));


            start = currentDeltaScore;
            currentDeltaScoreAnimated = Interpolator.LINEAR.interpolate(start, 0, progress);
            deltaScoreLabel.setText("+" + currentDeltaScoreAnimated);

            if (progress >= 1) {
                shouldAdjustDeltaLabel = false;

                currentTextCounter = 0;

                totalScoreLabel.setText("Score: " + scoreFormat.format(currentScoreFinal));
                deltaScoreLabel.setText("+" + 0);

                currentScore = currentScoreFinal;

                currentScore += currentDeltaScore;
                deltaFadeOutAnimation.play();
            }
        }
    }

    /**
     * Sets the label properties and starts the relevant fade in, and number animations
     *
     * @param deltaScore The score that the player has been awarded
     */
    private void handleScoreChange(int deltaScore) {
        if (currentDeltaScoreAnimated != 0) {
            shouldAdjustDeltaLabel = false;

            totalScoreLabel.setText("Score: " + scoreFormat.format(currentScore));
            currentScore = currentScoreFinal;

            currentTextCounter = 0;

            deltaScoreLabel.setTranslateY(0);
            deltaScoreLabel.setScaleX(0);
            deltaScoreLabel.setScaleY(0);
        }

        currentDeltaScore = deltaScore;
        currentScoreFinal = currentScore + deltaScore;
        totalScoreLabel.setText("Score: " + scoreFormat.format(currentScore));
        deltaScoreLabel.setText("+" + deltaScore);
        deltaFadeInAnimation.play();
    }

    /**
     * Updates the block queue on the right of the screen with the latest thumbnails
     *
     * @param colors The colors of the current blocks in the queue
     */
    private void handleBlockQueueChange(ArrayList<Block.Color> colors) {
        blockQueue.getChildren().clear();

        blockQueue.getChildren().add(upNextLabel);

        for (Block.Color color : colors) {
            ImageView image = new ImageView();
            image.setImage(blockThumbnails[color.ordinal()].get());
            image.setPreserveRatio(true);
            image.setFitWidth(thumbnailSize); // Can't adjust these values via CSS unfortunately
            image.maxWidth(thumbnailSize);
            image.getStyleClass().add("block-thumbnail");

            blockQueue.getChildren().add(image);
        }
    }

    private void handleFadeInFinish(ActionEvent event) {
        shouldAdjustDeltaLabel = true;
    }
}
