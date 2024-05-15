package com.neo.game.leaderboard;

import com.neo.game.settings.*;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigProperty;

import java.util.UUID;

/**
 * Represents the settings for the leaderboard (User specific)
 */
@Config(name = "leaderboard")
public class LeaderboardSettings {
    @ConfigProperty(section = "Notice")
    @ForceSerialize
    private boolean hasSeenInitialMessage;

    @ConfigProperty(section = "Connection")
    @ForceSerialize
    private boolean enabled;

    @ConfigProperty(section = "User")
    @ForceSerialize
    private String userIdentifier;

    @ConfigProperty(section = "User")
    @ForceSerialize
    private String username = System.getProperty("user.name");

    private String previousUsername;

    void setPreviousUsername(String previousUsername) {
        this.previousUsername = previousUsername;
    }

    String getPreviousUsername() {
        return previousUsername;
    }

    /**
     * Sets if the leaderboard should be enabled or not
     *
     * @param enabled Self-explanatory
     */
    public void setIsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Indicates if the leaderboard is enabled
     *
     * @return Is the leaderboard enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the user has seen the leaderboard intro message
     *
     * @param seenMessage Sets if the user has seen the intro
     */
    public void setHasSeenInitialMessage(boolean seenMessage) {
        hasSeenInitialMessage = seenMessage;
    }

    /**
     * Indicates if the user has seen the leaderboard intro message
     *
     * @return If the user has seen the intro message
     */
    public boolean hasSeenInitialMessage() {
        return hasSeenInitialMessage;
    }

    /**
     * Gets the unique identifier for the user
     *
     * @return Unique identifier
     */
    public String getUserIdentifier() {
        return userIdentifier;
    }

    /**
     * Sets the current username of the user
     *
     * @param username Username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the current username of the user
     *
     * @return Current username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets a settings category that can be configured by {@link SettingsUIComponent}
     *
     * @return Preconfigured category
     */
    public SettingCategory getSettingsCategory() {
        SettingCategory category = new SettingCategory();
        category.setName("Leaderboard");
        category.setSaveAction(this::save);

        SettingCategory connectionCategory = new SettingCategory();
        connectionCategory.setName("Connection");

        BooleanSetting isEnabledSetting = new BooleanSetting();
        isEnabledSetting.setName("Enabled");
        isEnabledSetting.setDefaultValue(false);
        isEnabledSetting.setValueGetter(this::isEnabled);
        isEnabledSetting.setValueSetter(this::setIsEnabled);

        connectionCategory.addChildren(isEnabledSetting);

        SettingCategory userCategory = new SettingCategory();
        userCategory.setName("User");

        StringSetting usernameSetting = new StringSetting(StringSetting.ValidationStyle.NOT_EMPTY);
        usernameSetting.setName("Username");
        usernameSetting.setDefaultValue(System.getProperty("user.name"));
        usernameSetting.setValueGetter(this::getUsername);
        usernameSetting.setValueSetter(this::setUsername);

        userCategory.addChildren(usernameSetting);

        category.addChildren(connectionCategory, userCategory);

        return category;
    }

    /**
     * Saves the current settings for the user
     */
    public void save() {
        if (userIdentifier.isEmpty())
            userIdentifier = UUID.randomUUID().toString();

        ConfigManager.saveConfig(this);
        LeaderboardService.getInstance().updateUsernameIfRequired();
    }
}
