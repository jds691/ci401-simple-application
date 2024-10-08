package com.neo.game;

import com.neo.game.audio.MusicComponent;
import com.neo.game.input.Input;
import com.neo.game.input.InputAction;
import com.neo.twig.Engine;
import com.neo.twig.events.Event;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.SceneService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager extends NodeComponent {
    private static final int BLOCKS_TO_QUEUE = 6;
    private final ArrayDeque<BlockFormation> blockQueue = new ArrayDeque<>();
    private static final int MAX_PATTERN_REPEATS = 3;
    private final int[] blockPatternHistory = new int[MAX_PATTERN_REPEATS];
    private int currentScore;
    private final Event<Integer> currentScoreDidChange = new Event<>();

    private BlockFormation storedBlock;

    private boolean gameIsOver;
    private final Event<EndReason> gameDidEnd = new Event<>();

    private final Event<Boolean> pauseDidChange = new Event<>();
    private final Event<ArrayList<Block.Color>> blockQueueDidChange = new Event<>();
    private boolean isPaused;
    private SceneService sceneService;
    private MusicComponent gameMusic;
    private static final float MUSIC_FADE_LENGTH = 3000;
    private MusicComponent gameOverMusic;
    private MusicComponent timesUpMusic;
    private MusicComponent countdownMusic;
    private boolean isInCountdown;
    private boolean musicTransitionActive;
    private float musicTransitionProgress;

    @Override
    public void start() {
        super.start();

        Arrays.fill(blockPatternHistory, -1);

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

        countdownMusic = sceneService.getActiveScene()
                .findRootNode("Countdown Music")
                .getComponent(MusicComponent.class);

        timesUpMusic = sceneService.getActiveScene()
                .findRootNode("Time Up Music")
                .getComponent(MusicComponent.class);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!gameIsOver && InputAction.get(Input.PAUSE).wasActivatedThisFrame()) {
            // Invoke pause event
            setIsPaused(!isPaused);

            if (isPaused) {
                countdownMusic.pause();
                gameMusic.pause();
            } else {
                if (isInCountdown && !musicTransitionActive) {
                    countdownMusic.play();
                } else {
                    gameMusic.play();
                }
            }
        }

        if (!isPaused && musicTransitionActive) {
            musicTransitionProgress += deltaTime;
            double progress = Math.clamp(musicTransitionProgress / MUSIC_FADE_LENGTH, 0, 1);

            gameMusic.setVolume((float) (1 - progress));
            if (progress >= 1) {
                countdownMusic.play();
                musicTransitionActive = false;
            }
        }

        if (!isPaused && musicTransitionActive) {
            musicTransitionProgress += deltaTime;
            double progress = Math.clamp(musicTransitionProgress / MUSIC_FADE_LENGTH, 0, 1);

            gameMusic.setVolume((float) (1 - progress));
            if (progress >= 1) {
                countdownMusic.play();
                musicTransitionActive = false;
            }
        }
    }

    /**
     * Gets an event that can be listened to, to determine the pause state of the game
     *
     * @return Event with arg, representing if the game is paused
     */
    public Event<Boolean> getPauseDidChangeEvent() {
        return pauseDidChange;
    }

    public void setIsPaused(boolean paused) {
        isPaused = paused;
        pauseDidChange.emit(isPaused);

        if (isPaused) {
            countdownMusic.pause();
            gameMusic.pause();
        } else {
            if (isInCountdown && !musicTransitionActive) {
                countdownMusic.play();
            } else {
                gameMusic.play();
            }
        }
    }

    /**
     * Gets an event that can be listened to, to emit when the game ends
     *
     * @return Event with no args
     */
    public Event<EndReason> getGameDidEndEvent() {
        return gameDidEnd;
    }

    /**
     * Gets an event that can be listened to, to get the latest delta updates to the score
     *
     * @return Event with integer arg, representing the latest score added
     */
    public Event<Integer> getCurrentScoreDidChangeEvent() {
        return currentScoreDidChange;
    }

    /**
     * Gets an event that can be listened to, to determine the pause state of the game
     *
     * @return Event with list arg, containing the colors of the blocks in the queue
     */
    public Event<ArrayList<Block.Color>> getBlockQueueDidChangeEvent() {
        return blockQueueDidChange;
    }

    /**
     * Gets the current queue of the blocks
     *
     * @return Queue of blocks
     */
    public ArrayDeque<BlockFormation> getBlockQueue() {
        return blockQueue;
    }

    /**
     * Requests a block from the block queue.
     *
     * <p>
     * It is automatically removed from the queue and a new block is generated in it's place
     * </p>
     *
     * @return First block in the current queue
     */
    public BlockFormation requestNextBlockFormationInQueue() {
        BlockFormation form = blockQueue.pop();

        blockQueue.add(requestRandomBlockFormation());

        ArrayList<Block.Color> blockQueueColors = new ArrayList<>(BLOCKS_TO_QUEUE);
        for (BlockFormation formation : blockQueue) {
            blockQueueColors.add(formation.color);
        }
        blockQueueDidChange.emit(blockQueueColors);

        return form;
    }

    /**
     * Requests a random block formation
     *
     * @return Random block formation
     */
    public BlockFormation requestRandomBlockFormation() {
        int index;

        do {
            index = ThreadLocalRandom.current().nextInt(0, BlockFormation.ALL.length);

            boolean valid = false;
            for (Integer historicIndex : blockPatternHistory) {
                if (index != historicIndex) {
                    valid = true;
                    break;
                }
            }

            if (valid)
                break;

        } while (true);

        for (int i = 0; i < MAX_PATTERN_REPEATS - 1; i++) {
            blockPatternHistory[i] = blockPatternHistory[i + 1];
        }
        blockPatternHistory[0] = index;

        return BlockFormation.ALL[index];
    }

    /**
     * Requests the manager to end the game
     */
    public void signalGameEnd() {
        signalGameEnd(EndReason.BOARD_FULL);
    }

    public void signalGameEnd(EndReason reason) {
        gameIsOver = true;
        gameMusic.stop();

        switch (reason) {
            case BOARD_FULL:
                countdownMusic.stop();
                gameOverMusic.play();
                break;
            case TIME:
                timesUpMusic.play();
                break;
        }

        gameDidEnd.emit(reason);
    }

    /**
     * Adds a score to the game
     *
     * @param score Score to add
     */
    public void addScore(int score) {
        currentScore += score;
        currentScoreDidChange.emit(score);
    }

    /**
     * Gets the current score of this game session
     *
     * @return Current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    public void beginMusicTransition() {
        isInCountdown = true;
        musicTransitionActive = true;
    }

    public enum EndReason {
        BOARD_FULL,
        TIME
    }
}
