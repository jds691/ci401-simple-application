package com.neo.game.title;

import com.neo.game.Block;
import com.neo.game.Block.Color;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.ImageResource;
import com.neo.twig.ui.FXComponent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.concurrent.ThreadLocalRandom;

public class TitleTetrisBlock extends FXComponent {
    private Block[][] initialBlockData = new Block[][]{
            {
                    generateRandomBlock(),
                    generateRandomBlock(),
                    generateRandomBlock(),
            },
            {
                    null,
                    generateRandomBlock(),
                    null,
            },
            {
                    null,
                    generateRandomBlock(),
                    null,
            }
    };

    @ForceSerialize
    private ImageResource[][] chipSprites;
    @ForceSerialize
    private double iconSize;

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setPickOnBounds(false);

        GridPane iconGrid = new GridPane();
        iconGrid.setMaxSize(iconSize, iconSize);

        for (int i = 0; i < initialBlockData.length; i++) {
            for (int j = 0; j < initialBlockData[i].length; j++) {
                Block data = initialBlockData[i][j];

                if (data == null)
                    continue;

                ImageView blockImage = new ImageView();
                blockImage.setImage(chipSprites[data.getColor().ordinal()][data.getTone()].get());
                blockImage.setFitWidth(iconSize / initialBlockData.length);
                blockImage.setPreserveRatio(true);
                blockImage.setSmooth(true);
                blockImage.setOnMouseClicked(this::handleBlockClick);

                blockImage.setUserData(data);

                iconGrid.add(blockImage, j, i);
            }
        }

        BorderPane.setAlignment(iconGrid, Pos.CENTER);
        root.setCenter(iconGrid);

        return root;
    }

    /**
     * Changes the color or tone of the click block on the title screen
     *
     * @param event Event associated with click
     */
    private void handleBlockClick(MouseEvent event) {
        ImageView blockImage = (ImageView) event.getSource();
        Block data = (Block) blockImage.getUserData();

        if (event.getButton() == MouseButton.PRIMARY) {
            int currentColorOrdinal = data.getColor().ordinal();
            currentColorOrdinal++;

            if (currentColorOrdinal == Block.Color.values().length)
                currentColorOrdinal = 1;

            data.setColor(Block.Color.values()[currentColorOrdinal]);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            int currentTone = data.getTone();
            currentTone++;

            if (currentTone == 3)
                currentTone = 0;

            data.setTone(currentTone);
        }

        blockImage.setImage(chipSprites[data.getColor().ordinal()][data.getTone()].get());

        blockImage.setUserData(data);
    }

    private Block generateRandomBlock() {
        Block block = new Block();

        int colorOrdinal = ThreadLocalRandom.current().nextInt(1, Color.values().length);

        block.setColor(Color.values()[colorOrdinal]);
        return block;
    }
}
