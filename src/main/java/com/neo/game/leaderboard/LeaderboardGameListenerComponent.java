package com.neo.game.leaderboard;

import com.neo.game.GameManager;
import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.twig.Engine;
import com.neo.twig.scene.NodeComponent;

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

            if (!leaderboard.getSettings().hasSeenInitialMessage()) {
                leaderboard.playIntro(true);
            }

            handleScoreUpload(manager.getCurrentScore());
        });
    }

    private void handleScoreUpload(int score) {
        if (!leaderboard.getSettings().isEnabled())
            return;

        leaderboard.uploadScore(score);
    }
}
