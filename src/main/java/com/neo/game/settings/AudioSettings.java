package com.neo.game.settings;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioBus;
import com.neo.twig.audio.AudioService;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;

@Config(name = "audio")
public class AudioSettings {
    private static AudioSettings instance;

    private AudioBus masterBus;
    private AudioBus musicBus;
    private AudioBus sfxBus;
    private AudioBus uiBus;
    @ForceSerialize
    private float masterVolume = 1;
    @ForceSerialize
    private float musicVolume = 1;
    @ForceSerialize
    private float sfxVolume = 1;
    @ForceSerialize
    private float uiVolume = 1;

    private AudioSettings() {
        AudioService audioService = Engine.getAudioService();

        masterBus = audioService.getAudioBus("Master");
        musicBus = audioService.getAudioBus("Master/Music");
        sfxBus = audioService.getAudioBus("Master/SFX");
        uiBus = audioService.getAudioBus("Master/UI");
    }

    public static AudioSettings getInstance() {
        if (instance == null) {
            instance = new AudioSettings();
        }

        return instance;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getUiVolume() {
        return uiVolume;
    }
    
    /**
     * Gets a settings category that can be configured by {@link SettingsUIComponent}
     *
     * @return Preconfigured category
     */
    public SettingCategory getSettingsCategory() {
        SettingCategory category = new SettingCategory();
        category.setName("Audio");

        FloatSetting masterVolumeSetting = new FloatSetting();
        masterVolumeSetting.setName("Master Volume");
        masterVolumeSetting.setDefaultValue(1.0f);
        masterVolumeSetting.setValueGetter(this::getMasterVolume);
        masterVolumeSetting.setValueSetter((volume) -> {
            masterVolume = volume;
        });

        FloatSetting musicVolumeSetting = new FloatSetting();
        musicVolumeSetting.setName("Music Volume");
        musicVolumeSetting.setDefaultValue(1.0f);
        musicVolumeSetting.setValueGetter(this::getMusicVolume);
        musicVolumeSetting.setValueSetter((volume) -> {
            musicVolume = volume;
        });

        FloatSetting sfxVolumeSetting = new FloatSetting();
        sfxVolumeSetting.setName("SFX Volume");
        sfxVolumeSetting.setDefaultValue(1.0f);
        sfxVolumeSetting.setValueGetter(this::getSfxVolume);
        sfxVolumeSetting.setValueSetter((volume) -> {
            sfxVolume = volume;
        });

        FloatSetting uiVolumeSetting = new FloatSetting();
        uiVolumeSetting.setName("UI Volume");
        uiVolumeSetting.setDefaultValue(1.0f);
        uiVolumeSetting.setValueGetter(this::getUiVolume);
        uiVolumeSetting.setValueSetter((volume) -> {
            uiVolume = volume;
        });

        category.addChildren(
                masterVolumeSetting,
                musicVolumeSetting,
                sfxVolumeSetting,
                uiVolumeSetting
        );
        category.setSaveAction(this::save);

        return category;
    }

    /**
     * Saves the current settings for the user
     */
    public void save() {
        ConfigManager.saveConfig(this);

        apply();
    }

    public void apply() {
        masterBus.setVolume(masterVolume);
        musicBus.setVolume(musicVolume);
        sfxBus.setVolume(sfxVolume);
        uiBus.setVolume(uiVolume);
    }
}
