package com.neo.game.ui;

import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.RenderComponent;
import com.neo.twig.logger.Logger;
import com.neo.twig.resources.ImageResource;
import javafx.scene.canvas.GraphicsContext;

public class MinuteRemainingOverlayComponent extends RenderComponent {
    private static final Logger logger = Logger.getFor(MinuteRemainingOverlayComponent.class);

    @ForceSerialize
    private ImageResource sprite;

    /*
    Stages:
    - Mask in
    - Scale and pulse
    - Mask out
    */
    @ForceSerialize
    private double[] effectStagesLength;
    private int currentStage = 0;

    private float currentDelta;
    private boolean enabled = false;
    private boolean paused = false;

    @Override
    public void start() {
        super.start();

        Engine.getSceneService()
                .getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class)
                .getPauseDidChangeEvent()
                .addHandler((paused) -> {
                    this.paused = paused;
                });
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!enabled || paused)
            return;

        currentDelta += deltaTime;
    }

    @Override
    protected void drawToContext(GraphicsContext context) {
        if (!enabled)
            return;

        context.save();

        switch (currentStage) {
            case 0 -> drawFadeIn(context);
            case 1 -> drawScale(context);
            case 2 -> drawFadeOut(context);
        }

        context.restore();

        if (currentStage == effectStagesLength.length) {
            enabled = false;
            currentStage = 0;
            currentDelta = 0;
        }
    }

    private void drawFadeIn(GraphicsContext context) {
        double progress = getCurrentRenderStageProgress();

        context.setGlobalAlpha(progress);
        context.drawImage(sprite.get(), 240 - (sprite.get().getWidth() / 2), 320 - (sprite.get().getHeight() / 2));

        if (progress >= 1) {
            currentStage++;
            logger.logDebug("Current render stage = " + currentStage);
        }
    }

    private void drawScale(GraphicsContext context) {
        double progress = getCurrentRenderStageProgress();

        context.setGlobalAlpha(1 - progress);
        context.drawImage(sprite.get(), 240 - (sprite.get().getWidth() / 2), 320 - (sprite.get().getHeight() / 2));

        if (progress >= 1) {
            currentStage++;
            logger.logDebug("Current render stage = " + currentStage);
        }
    }

    private void drawFadeOut(GraphicsContext context) {
        double progress = getCurrentRenderStageProgress();

        context.setGlobalAlpha(1 - progress);
        context.drawImage(sprite.get(), 240 - (sprite.get().getWidth() / 2), 320 - (sprite.get().getHeight() / 2));

        if (progress >= 1) {
            currentStage++;
            logger.logDebug("Current render stage = " + currentStage);
        }
    }

    private double getCurrentRenderStageProgress() {
        return Math.clamp(currentDelta / effectStagesLength[currentStage], 0, 1);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
