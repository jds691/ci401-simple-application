package com.neo.game;

import com.neo.twig.graphics.RenderComponent;
import javafx.scene.canvas.GraphicsContext;

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

    @Override
    public void start() {
        super.start();

        dataComponent = getNode().getComponent(BoardDataComponent.class);
    }

    @Override
    protected void drawToContext(GraphicsContext context) {

    }
}
