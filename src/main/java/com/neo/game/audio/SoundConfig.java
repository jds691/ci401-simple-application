package com.neo.game.audio;

import com.neo.game.title.TitleManagerComponent;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;

import java.net.URL;

@Config(name = "sound")
public class SoundConfig {
    private static SoundConfig instance;
    @ForceSerialize
    private URL titleTheme;

    private SoundConfig() {
        titleTheme = TitleManagerComponent.class.getResource("MUS_dev_Title.wav");
    }

    public static SoundConfig getInstance() {
        if (instance == null) {
            instance = new SoundConfig();
            ConfigManager.loadConfig(instance);
        }

        return instance;
    }

    public URL getMusicLocation(String key) {
        return switch (key) {
            case "titleTheme" -> titleTheme;
            default -> null;
        };
    }
}
