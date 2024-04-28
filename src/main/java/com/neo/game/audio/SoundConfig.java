package com.neo.game.audio;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;

import java.net.URL;

@Config(name = "sound")
public class SoundConfig {
    private static SoundConfig instance;

    @ForceSerialize
    private URL titleTheme;

    @ForceSerialize
    private URL bgmGame;

    @ForceSerialize
    private URL blockPlace;

    private SoundConfig() {
        titleTheme = MusicComponent.class.getResource("MUS_Title.wav");
        bgmGame = MusicComponent.class.getResource("MUS_Game.mp3");

        blockPlace = SoundConfig.class.getResource("SFX_block_place.wav");
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
            case "BGM_Game" -> bgmGame;
            default -> null;
        };
    }

    public URL getSFXLocation(String key) {
        return switch (key) {
            case "SFX_blockPlace" -> blockPlace;
            default -> null;
        };
    }
}
