package com.neo.game;

import com.neo.game.input.InputAction;
import com.neo.twig.events.Event;
import com.neo.twig.scene.NodeComponent;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager extends NodeComponent {
    private static final int BLOCKS_TO_QUEUE = 5;
    private final ArrayDeque<BlockFormation> blockQueue = new ArrayDeque<>();
    private int currentScore;
    private BlockFormation storedBlock;

    private final Event<Boolean> pauseDidChange = new Event<>();
    private boolean isPaused;

    @Override
    public void start() {
        super.start();

        for (int i = 0; i < BLOCKS_TO_QUEUE; i++) {
            blockQueue.add(requestRandomBlockFormation());
        }
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

    public BlockFormation requestNextBlockFormationInQueue() {
        BlockFormation form = blockQueue.pop();

        blockQueue.add(requestRandomBlockFormation());

        return form;
    }

    public BlockFormation requestRandomBlockFormation() {
        int index = ThreadLocalRandom.current().nextInt(0, BlockFormation.ALL.length);

        return BlockFormation.ALL[index];
    }
}
