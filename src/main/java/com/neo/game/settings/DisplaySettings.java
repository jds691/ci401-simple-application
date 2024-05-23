package com.neo.game.settings;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigProperty;
import javafx.scene.input.KeyCode;

@Config(name = "display")
public class DisplaySettings {
    private static DisplaySettings instance;

    @ForceSerialize
    @ConfigProperty(section = "Grid")
    private boolean displayGridLines;

    private DisplaySettings() {
        displayGridLines = true;
    }

    public static DisplaySettings getInstance() {
        if (instance == null) {
            instance = new DisplaySettings();
        }

        return instance;
    }

    public boolean getDisplayGridLines() {
        return displayGridLines;
    }

    /**
     * Gets a settings category that can be configured by {@link SettingsUIComponent}
     *
     * @return Preconfigured category
     */
    public SettingCategory getSettingsCategory() {
        SettingCategory category = new SettingCategory();
        category.setName("Display");

        SettingCategory gridCategory = new SettingCategory();
        gridCategory.setName("Grid");

        BooleanSetting displayGridLinesSettings = new BooleanSetting();
        displayGridLinesSettings.setName("Display Grid Lines");
        displayGridLinesSettings.setDefaultValue(true);
        displayGridLinesSettings.setValueGetter(this::getDisplayGridLines);
        displayGridLinesSettings.setValueSetter((display) -> {
            displayGridLines = display;
        });

        gridCategory.addChildren(displayGridLinesSettings);

        category.addChildren(gridCategory);
        category.setSaveAction(this::save);

        return category;
    }

    /**
     * Saves the current settings for the user
     */
    public void save() {
        ConfigManager.saveConfig(this);
    }
}
