package com.neo.game.ui;

import com.neo.game.Block;
import com.neo.game.BlockFormation;
import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.ImageResource;
import com.neo.twig.ui.FXComponent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class InGameUIComponent extends FXComponent {
    private GameManager gameManager;

    DecimalFormat scoreFormat = new DecimalFormat("0000000000");
    private int currentScore;

    private Label upNextLabel;
    private Label totalScoreLabel;
    private Label deltaScoreLabel;
    private VBox blockQueue;

    @ForceSerialize
    private double thumbnailSize;

    @ForceSerialize
    private ImageResource[] blockThumbnails;

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

        VBox scoreInfo = new VBox();

        totalScoreLabel = new Label("Score: 0000000000");
        totalScoreLabel.getStyleClass().add("label");
        deltaScoreLabel = new Label("+000");
        deltaScoreLabel.getStyleClass().add("label");

        scoreInfo.getChildren().add(totalScoreLabel);
        scoreInfo.getChildren().add(deltaScoreLabel);

        root.setTop(scoreInfo);

        return root;
    }

    private void handleScoreChange(int deltaScore) {
        currentScore += deltaScore;
        totalScoreLabel.setText("Score: " + scoreFormat.format(currentScore));
        deltaScoreLabel.setText("+" + deltaScore);
    }

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
}
