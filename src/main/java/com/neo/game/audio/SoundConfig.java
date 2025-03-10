package com.neo.game.audio;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigProperty;
import com.neo.twig.resources.ResourcePath;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A configuration class that allows all the sounds used in the game to be remapped to other files.
 */
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
    @ConfigProperty(section = "SFX")
    private URL blockPlace;
    @ForceSerialize
    @ConfigProperty(section = "SFX")
    private URL blockRotate;
    @ForceSerialize
    @ConfigProperty(section = "SFX")
    private URL blockRotateInvalid;
    @ForceSerialize
    @ConfigProperty(section = "SFX")
    private URL lineClear;

    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL pauseOpen;
    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL pauseClose;
    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL resume;
    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL quit;

    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL messageShow;
    @ForceSerialize
    @ConfigProperty(section = "UI")
    private URL messageHide;

    private SoundConfig() {
        try {
            titleTheme = ResourcePath.resolveAssetPath("audio/MUS_Title.mp3").toFile().toURL();
            bgmGame = ResourcePath.resolveAssetPath("audio/MUS_Game.mp3").toFile().toURL();
            bgmGameOver = ResourcePath.resolveAssetPath("audio/MUS_Game_Over.mp3").toFile().toURL();

            blockPlace = ResourcePath.resolveAssetPath("audio/SFX_block_place.wav").toFile().toURL();
            blockRotate = ResourcePath.resolveAssetPath("audio/SFX_block_rotate.wav").toFile().toURL();
            blockRotateInvalid = ResourcePath.resolveAssetPath("audio/SFX_block_rotate_invalid.wav").toFile().toURL();
            lineClear = ResourcePath.resolveAssetPath("audio/SFX_line_clear.wav").toFile().toURL();

            pauseOpen = ResourcePath.resolveAssetPath("audio/SFX_Pause_Open.wav").toFile().toURL();
            pauseClose = ResourcePath.resolveAssetPath("audio/SFX_Pause_Close.wav").toFile().toURL();
            resume = ResourcePath.resolveAssetPath("audio/SFX_Resume.wav").toFile().toURL();
            quit = ResourcePath.resolveAssetPath("audio/SFX_Quit_Fade.wav").toFile().toURL();
            messageShow = ResourcePath.resolveAssetPath("audio/UI_message_show.wav").toFile().toURL();
            messageHide = ResourcePath.resolveAssetPath("audio/UI_message_hide.wav").toFile().toURL();
        } catch (MalformedURLException ignored) {
            //NOTE: This should not occur
        }
    }

    /**
     * Gets (or constructs) the singleton instance of this object.
     *
     * @return Singleton instance
     */
    public static SoundConfig getInstance() {
        if (instance == null) {
            instance = new SoundConfig();
        }

        return instance;
    }

    /**
     * Gets the URL of a requested song file based on the input key.
     *
     * @param key Unique key given to the song file for lookups
     * @return URL of requested song or null
     */
    public URL getMusicLocation(String key) {
        return switch (key) {
            case "titleTheme" -> titleTheme;
            case "BGM_Game" -> bgmGame;
            case "BGM_Game_Over" -> bgmGameOver;
            default -> null;
        };
    }

    /**
     * Gets the URL of a requested SFX file based on the input key.
     *
     * @param key Unique key given to the SFX file for lookups
     * @return URL of requested SFX or null
     */
    public URL getSFXLocation(String key) {
        return switch (key) {
            case "SFX_blockPlace" -> blockPlace;
            case "SFX_blockRotate" -> blockRotate;
            case "SFX_blockRotate_invalid" -> blockRotateInvalid;
            case "SFX_lineClear" -> lineClear;
            case "SFX_Pause_Open" -> pauseOpen;
            case "SFX_Pause_Close" -> pauseClose;
            case "SFX_Resume" -> resume;
            case "SFX_Quit" -> quit;
            case "UI_message_show" -> messageShow;
            case "UI_message_hide" -> messageHide;
            default -> null;
        };
    }
}
