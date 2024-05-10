package com.neo.game;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.RenderComponent;
import com.neo.twig.resources.ImageResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.io.IOException;

/**
 * Responsible for rendering the current blocks in the board.
 *
 * <p>
 * The board needs a dedicated component as it is likely to be better to directly draw the blocks.<br>
 * Rather than drawing various sprites of all different configurations.
 * </p>
 */
public class BoardRenderComponent extends RenderComponent {
    private BoardDataComponent dataComponent;

    public float blockSize = 30;
    @ForceSerialize
    private ImageResource boardBackground;
    private ImagePattern backgroundPattern;

    private String[][] chipSpriteUrls;
    private Image[][] chipSprites;

    @Override
    public void start() {
        super.start();

        backgroundPattern = new ImagePattern(boardBackground.get(), 0, 0, 30, 30, false);

        dataComponent = getNode().getComponent(BoardDataComponent.class);

        initialiseChipSprites();
    }

    @Override
    protected void drawToContext(GraphicsContext context) {
        /*
        Draw steps:
        - Draw board background
        - Draw board contents based on dataComponent (use the Side Order chips sprite)
        - Draw board border
        - Draw required effects
        - Draw transitions?
        */

        context.save();
        context.setFill(backgroundPattern);
        context.fillRect(0, 0, BoardDataComponent.BOARD_WIDTH * blockSize + BoardDataComponent.BOARD_WIDTH, BoardDataComponent.BOARD_HEIGHT * blockSize + BoardDataComponent.BOARD_HEIGHT);

        for (int i = 0; i < BoardDataComponent.BOARD_HEIGHT; i++) {
            for (int j = 0; j < BoardDataComponent.BOARD_WIDTH; j++) {
                Block state = dataComponent.getBoardState(j, i);

                if (state.color == Block.Color.None)
                    continue;

                int colorIndex = state.color.ordinal();
                int tone = state.tone;

                context.drawImage(chipSprites[colorIndex][tone], j * blockSize + j, i * blockSize + i, blockSize, blockSize);
            }
        }

        // Effects
        // Gradient overlay: https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/scene/paint/LinearGradient.html
        context.restore();
    }

    // We are never unfolding this method again. If something breaks I don't care
    // This is an insane way to load images
    private void initialiseChipSprites() {
        chipSprites = new Image[7][3];

        try {
            chipSprites[1][0] = new Image(
                    BoardRenderComponent.class.getResource("block_blue_0.png").openStream()
            );
            chipSprites[1][1] = new Image(
                    BoardRenderComponent.class.getResource("block_blue_1.png").openStream()
            );
            chipSprites[1][2] = new Image(
                    BoardRenderComponent.class.getResource("block_blue_2.png").openStream()
            );

            chipSprites[2][0] = new Image(
                    BoardRenderComponent.class.getResource("block_cyan_0.png").openStream()
            );
            chipSprites[2][1] = new Image(
                    BoardRenderComponent.class.getResource("block_cyan_1.png").openStream()
            );
            chipSprites[2][2] = new Image(
                    BoardRenderComponent.class.getResource("block_cyan_2.png").openStream()
            );

            chipSprites[3][0] = new Image(
                    BoardRenderComponent.class.getResource("block_orange_0.png").openStream()
            );
            chipSprites[3][1] = new Image(
                    BoardRenderComponent.class.getResource("block_orange_1.png").openStream()
            );
            chipSprites[3][2] = new Image(
                    BoardRenderComponent.class.getResource("block_orange_2.png").openStream()
            );

            chipSprites[4][0] = new Image(
                    BoardRenderComponent.class.getResource("block_purple_0.png").openStream()
            );
            chipSprites[4][1] = new Image(
                    BoardRenderComponent.class.getResource("block_purple_1.png").openStream()
            );
            chipSprites[4][2] = new Image(
                    BoardRenderComponent.class.getResource("block_purple_2.png").openStream()
            );

            chipSprites[5][0] = new Image(
                    BoardRenderComponent.class.getResource("block_red_0.png").openStream()
            );
            chipSprites[5][1] = new Image(
                    BoardRenderComponent.class.getResource("block_red_1.png").openStream()
            );
            chipSprites[5][2] = new Image(
                    BoardRenderComponent.class.getResource("block_red_2.png").openStream()
            );

            chipSprites[6][0] = new Image(
                    BoardRenderComponent.class.getResource("block_yellow_0.png").openStream()
            );
            chipSprites[6][1] = new Image(
                    BoardRenderComponent.class.getResource("block_yellow_1.png").openStream()
            );
            chipSprites[6][2] = new Image(
                    BoardRenderComponent.class.getResource("block_yellow_2.png").openStream()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
