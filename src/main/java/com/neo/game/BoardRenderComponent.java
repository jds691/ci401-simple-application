package com.neo.game;

import com.neo.twig.Engine;
import com.neo.twig.TransformComponent;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.RenderComponent;
import com.neo.twig.resources.ImageResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

import java.util.ArrayList;

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

    private ArrayList<Integer> linesToEffect;
    private Effect currentEffect = Effect.NONE;
    private float effectDeltaTime;
    @ForceSerialize
    private double[] effectLength;

    private boolean gameIsPaused;

    @Override
    public void start() {
        super.start();

        transform = getNode().getComponent(TransformComponent.class);
        dataComponent = getNode().getComponent(BoardDataComponent.class);
        dataComponent.getLinesDidClearEvent().addHandler((clearedLines) -> {
            dataComponent.setPauseUpdates(true);

            linesToEffect = clearedLines;
            currentEffect = Effect.LINE_FILL;
            effectDeltaTime = 0;
        });
        Engine.getSceneService().getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class)
                .getPauseDidChangeEvent()
                .addHandler((paused) -> {
                    gameIsPaused = paused;
                });

        backgroundPattern = new ImagePattern(boardBackground.get(), transform.x, transform.y, 30, 30, false);
    }

    @Override
    public void update(float deltaTime) {
        if (!gameIsPaused)
            effectDeltaTime += deltaTime;
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

                if (currentEffect == Effect.FADE_OUT && linesToEffect.contains(i))
                    context.setGlobalAlpha(effectDeltaTime / effectLength[currentEffect.ordinal()]);

                context.drawImage(chipSprites[colorIndex][tone].get(), transform.x + (j * blockSize + j), transform.y + (i * blockSize + i), blockSize, blockSize);
            }

            context.setGlobalAlpha(1);
        }

        // Effects
        // Gradient overlay: https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/scene/paint/LinearGradient.html
        switch (currentEffect) {
            case LINE_FILL -> drawLineFillEffect(context);
            case FADE_OUT -> drawFadeOutEffect(context);
        }

        context.restore();
    }

    private void drawLineFillEffect(GraphicsContext context) {
        double progress = effectDeltaTime / effectLength[currentEffect.ordinal()];

        Stop[] stops;

        if (progress < 0.5) {
            stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(progress * 2, Color.WHITE), new Stop(progress * 2 + 0.1, Color.TRANSPARENT), new Stop(1, Color.TRANSPARENT)};
        } else {
            stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, Color.WHITE)};
        }

        LinearGradient effectGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        context.setFill(effectGradient);

        for (int clearedLine : linesToEffect) {
            context.fillRect(transform.x, transform.y + (clearedLine * blockSize + clearedLine), (BoardDataComponent.BOARD_WIDTH * blockSize) + BoardDataComponent.BOARD_WIDTH, blockSize + 2);
        }

        if (progress >= 1) {
            dataComponent.setPauseUpdates(false);
            effectDeltaTime = 0;
            currentEffect = Effect.FADE_OUT;
        }
    }

    private void drawFadeOutEffect(GraphicsContext context) {
        double progress = effectDeltaTime / effectLength[currentEffect.ordinal()];

        context.setFill(new Color(1, 1, 1, 1 - progress));
        for (int clearedLine : linesToEffect) {
            context.fillRect(transform.x, transform.y + (clearedLine * blockSize + clearedLine), (BoardDataComponent.BOARD_WIDTH * blockSize) + BoardDataComponent.BOARD_WIDTH, blockSize + 2);
        }

        if (progress >= 1) {
            effectDeltaTime = 0;
            currentEffect = Effect.NONE;
        }
    }

    enum Effect {
        NONE,
        LINE_FILL,
        FADE_OUT
    }
}
