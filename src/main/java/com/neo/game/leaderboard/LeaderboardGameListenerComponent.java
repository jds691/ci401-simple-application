package com.neo.game.leaderboard;

import com.neo.game.GameManager;
import com.neo.twig.Engine;
import com.neo.twig.scene.NodeComponent;

/**
 * Used to listen for when a game session ends so the score can be uploaded to the leaderboard
 */
public class LeaderboardGameListenerComponent extends NodeComponent {
    LeaderboardService leaderboard;

    @Override
    public void start() {
        super.start();

        leaderboard = LeaderboardService.getInstance();

        GameManager manager = Engine.getSceneService().getActiveScene()
                .findRootNode("Game Context")
                .getComponent(GameManager.class);

        manager.getGameDidEndEvent().addHandler((ignored) -> {
            if (manager.getCurrentScore() == 0)
                return;

            /*if (!leaderboard.getSettings().hasSeenInitialMessage()) {
                leaderboard.playIntro(true);
            }*/

            handleScoreUpload(manager.getCurrentScore());
        });
    }

    private void handleScoreUpload(int score) {
        if (!leaderboard.getSettings().isEnabled())
            return;

        leaderboard.uploadScore(score, "test");
    }
}
