package com.neo.game;

import com.neo.twig.TransformComponent;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.RenderComponent;
import com.neo.twig.resources.ImageResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.ImagePattern;

/**
 * Responsible for rendering the current blocks in the board.
 *
 * <p>
 * The board needs a dedicated component as it is likely to be better to directly draw the blocks.<br>
 * Rather than drawing various sprites of all different configurations.
 * </p>
 */
public class BoardRenderComponent extends RenderComponent {
    private TransformComponent transform;
    private BoardDataComponent dataComponent;

    public float blockSize = 30;
    @ForceSerialize
    private ImageResource boardBackground;
    private ImagePattern backgroundPattern;

    @ForceSerialize
    private ImageResource[][] chipSprites;

    @Override
    public void start() {
        super.start();

        transform = getNode().getComponent(TransformComponent.class);
        dataComponent = getNode().getComponent(BoardDataComponent.class);

        backgroundPattern = new ImagePattern(boardBackground.get(), transform.x, transform.y, 30, 30, false);
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
        context.fillRect(transform.x, transform.y, BoardDataComponent.BOARD_WIDTH * blockSize + BoardDataComponent.BOARD_WIDTH, BoardDataComponent.BOARD_HEIGHT * blockSize + BoardDataComponent.BOARD_HEIGHT);

        for (int i = 0; i < BoardDataComponent.BOARD_HEIGHT; i++) {
            for (int j = 0; j < BoardDataComponent.BOARD_WIDTH; j++) {
                Block state = dataComponent.getBoardState(j, i);

                if (state.color == Block.Color.None)
                    continue;

                int colorIndex = state.color.ordinal();
                int tone = state.tone;

                context.drawImage(chipSprites[colorIndex][tone].get(), transform.x + (j * blockSize + j), transform.y + (i * blockSize + i), blockSize, blockSize);
            }
        }

        // Effects
        // Gradient overlay: https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/scene/paint/LinearGradient.html
        context.restore();
    }
}
