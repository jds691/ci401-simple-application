package com.neo.game.title;

import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.NodeComponent;

public class LocalDBInitialiserComponent extends NodeComponent {
    @ForceSerialize
    private URLResource titleScene;

    @Override
    public void start() {
        super.start();

        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "System",
                        "Initialising local database"
                )
        );

        try {
            LeaderboardService.getInstance().initialiseLocalDB();

            MessageServiceComponent.getInstance().stopCurrentMessage();
            MessageServiceComponent.getInstance().addToQueue(
                    new Message(
                            "System",
                            "Local database load complete",
                            new MessageOption("Ok", (ignored) -> Engine.getSceneService().setScene(titleScene.get())))
            );
        } catch (Exception ignored) {
            MessageServiceComponent.getInstance().stopCurrentMessage();
            MessageServiceComponent.getInstance().addToQueue(
                    new Message(
                            "System",
                            "Unable to initialise the local database",
                            new MessageOption("Ok", (ignoredEvent) -> Engine.getSceneService().setScene(titleScene.get())))
            );
        }
    }
}
