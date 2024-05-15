package com.neo.game;

import com.neo.game.audio.SoundConfig;
import com.neo.game.input.InputAction;
import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.settings.KeyBindSettings;
import com.neo.game.title.TitleControllerComponent;
import com.neo.twig.AppConfig;
import com.neo.twig.Engine;
import com.neo.twig.EngineConfig;
import com.neo.twig.audio.AudioBus;
import com.neo.twig.audio.AudioConfig;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigScope;
import com.neo.twig.graphics.GraphicsConfig;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameApplication {
    public static void main(String[] args) {
        EngineConfig engineConfig = new EngineConfig(
                args,
                generateAppConfig(),
                generateGraphicsConfig(),
                generateAudioConfig()
        );

        Engine.init(engineConfig);

        InputAction.initialiseActions();
        ConfigManager.saveConfig(KeyBindSettings.getInstance(), ConfigScope.Engine);
        ConfigManager.loadConfig(KeyBindSettings.getInstance());

        ConfigManager.saveConfig(SoundConfig.getInstance(), ConfigScope.Engine);
        ConfigManager.loadConfig(SoundConfig.getInstance());
        // Force all fonts to be smoothed out engine wide
        System.setProperty("prism.lcdtext", "false");

        Engine.start();

        LeaderboardService.getInstance().shutdown();
    }

    private static AppConfig generateAppConfig() {
        AppConfig app = new AppConfig();

        app.name = "Tetris";
        app.version = "1.0-SNAPSHOT";
        app.initialScene = TitleControllerComponent.class.getResource("title.branch");
        app.icon = new Image(GameApplication.class.getResourceAsStream("icon.png"));

        return app;
    }

    private static GraphicsConfig generateGraphicsConfig() {
        GraphicsConfig graphics = new GraphicsConfig();

        graphics.height = 640;
        graphics.width = 480;
        graphics.clearColor = new Color((double) 243 / 255, (double) 233 / 255, (double) 229 / 255, 1);

        return graphics;
    }

    private static AudioConfig generateAudioConfig() {
        AudioConfig audio = new AudioConfig();

        AudioBus master = new AudioBus("Master");

        AudioBus music = new AudioBus("Music");
        AudioBus sfx = new AudioBus("SFX");
        AudioBus ui = new AudioBus("UI");

        master.addChildBus(music);
        master.addChildBus(sfx);
        master.addChildBus(ui);

        audio.mixerTree = master;

        return audio;
    }
}
