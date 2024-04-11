package com.neo.game.testing;

import com.neo.twig.AppConfig;
import com.neo.twig.Engine;
import com.neo.twig.EngineConfig;
import com.neo.twig.audio.AudioConfig;
import com.neo.twig.graphics.GraphicsConfig;

public class TestingApplication {
    public static void main(String[] args) {
        EngineConfig engineConfig = new EngineConfig(
                args,
                generateAppConfig(),
                generateGraphicsConfig(),
                generateAudioConfig()
        );

        Engine.init(engineConfig);

        Engine.start();
    }

    private static AppConfig generateAppConfig() {
        AppConfig app = new AppConfig();

        app.name = "Tetris";
        app.version = "- Testing";
        app.initialScene = TestingApplication.class.getResource("board-test.branch");

        return app;
    }

    private static GraphicsConfig generateGraphicsConfig() {
        GraphicsConfig graphics = new GraphicsConfig();

        graphics.height = 640;
        graphics.width = 480;

        return graphics;
    }

    private static AudioConfig generateAudioConfig() {
        AudioConfig audio = new AudioConfig();

        return audio;
    }
}
