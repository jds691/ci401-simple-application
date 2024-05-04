package com.neo.game;

import com.neo.game.audio.MusicComponent;
import com.neo.game.input.InputAction;
import com.neo.twig.Engine;
import com.neo.twig.events.Event;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager extends NodeComponent {
    private static final int BLOCKS_TO_QUEUE = 5;
    private final ArrayDeque<BlockFormation> blockQueue = new ArrayDeque<>();
    private int currentScore;
    private BlockFormation storedBlock;

    private final Event<Object> gameDidEnd = new Event<>();

    private final Event<Boolean> pauseDidChange = new Event<>();
    private boolean isPaused;
    private SceneService sceneService;
    private MusicComponent gameMusic;
    private MusicComponent gameOverMusic;

    @Override
    public void start() {
        super.start();

        for (int i = 0; i < BLOCKS_TO_QUEUE; i++) {
            blockQueue.add(requestRandomBlockFormation());
        }

        sceneService = Engine.getSceneService();

        gameMusic = sceneService.getActiveScene()
                .findRootNode("Game Music")
                .getComponent(MusicComponent.class);

        gameOverMusic = sceneService.getActiveScene()
                .findRootNode("Game Over Music")
                .getComponent(MusicComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (InputAction.PAUSE.wasActivatedThisFrame()) {
            // Invoke pause event
            isPaused = !isPaused;
            pauseDidChange.emit(isPaused);
        }
    }

    public Event<Boolean> getPauseDidChangeEvent() {
        return pauseDidChange;
    }

    public Event<Object> getGameDidEndEvent() {
        return gameDidEnd;
    }

    public BlockFormation requestNextBlockFormationInQueue() {
        BlockFormation form = blockQueue.pop();

        blockQueue.add(requestRandomBlockFormation());

        return form;
    }

    public BlockFormation requestRandomBlockFormation() {
        int index = ThreadLocalRandom.current().nextInt(0, BlockFormation.ALL.length);

        return BlockFormation.ALL[index];
    }

    public void signalGameEnd() {
        gameMusic.stop();
        gameOverMusic.resume();

        gameDidEnd.emit(null);
    }
}
