package com.neo.game.audio;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigProperty;

import java.net.URL;

@Config(name = "sound")
public class SoundConfig {
    private static SoundConfig instance;

    @ForceSerialize
    @ConfigProperty(section = "Music")
    private URL titleTheme;

    @ForceSerialize
    @ConfigProperty(section = "Music")
    private URL bgmGame;

    @ForceSerialize
    @ConfigProperty(section = "Music")
    private URL bgmGameOver;

    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL blockPlace;
    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL blockRotate;

    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL lineClear;

    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL pauseOpen;
    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL pauseClose;
    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL resume;
    @ForceSerialize
    @ConfigProperty(section = "Sound Effects")
    private URL quit;

    private SoundConfig() {
        titleTheme = MusicComponent.class.getResource("MUS_Title.mp3");
        bgmGame = MusicComponent.class.getResource("MUS_Game.mp3");
        bgmGameOver = MusicComponent.class.getResource("MUS_Game_Over.mp3");

        blockPlace = SoundConfig.class.getResource("SFX_block_place.wav");
        blockRotate = SoundConfig.class.getResource("SFX_block_rotate.wav");
        lineClear = SoundConfig.class.getResource("SFX_line_clear.wav");

        pauseOpen = SoundConfig.class.getResource("SFX_Pause_Open.wav");
        pauseClose = SoundConfig.class.getResource("SFX_Pause_Close.wav");
        resume = SoundConfig.class.getResource("SFX_Resume.wav");
        quit = SoundConfig.class.getResource("SFX_Quit_Fade.wav");
    }

    public static SoundConfig getInstance() {
        if (instance == null) {
            instance = new SoundConfig();
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
            case "SFX_blockRotate" -> blockRotate;
            case "SFX_lineClear" -> lineClear;
            case "SFX_Pause_Open" -> pauseOpen;
            case "SFX_Pause_Close" -> pauseClose;
            case "SFX_Resume" -> resume;
            case "SFX_Quit" -> quit;
            default -> null;
        };
    }
}
