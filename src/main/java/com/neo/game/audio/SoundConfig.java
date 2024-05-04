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
    private URL bgmGameOver;

    @ForceSerialize
    private URL blockPlace;

    @ForceSerialize
    private URL lineClear;

    private SoundConfig() {
        titleTheme = MusicComponent.class.getResource("MUS_Title.wav");
        bgmGame = MusicComponent.class.getResource("MUS_Game.mp3");
        bgmGameOver = MusicComponent.class.getResource("MUS_Game_Over.mp3");

        blockPlace = SoundConfig.class.getResource("SFX_block_place.wav");
        lineClear = SoundConfig.class.getResource("SFX_line_clear.wav");
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
            case "BGM_Game_Over" -> bgmGameOver;
            default -> null;
        };
    }

    public URL getSFXLocation(String key) {
        return switch (key) {
            case "SFX_blockPlace" -> blockPlace;
            case "SFX_lineClear" -> lineClear;
            default -> null;
        };
    }
}
