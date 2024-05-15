package com.neo.game.leaderboard;

import com.neo.game.settings.*;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigProperty;

import java.util.UUID;

@Config(name = "leaderboard")
public class LeaderboardSettings {
    @ConfigProperty(section = "Notice")
    @ForceSerialize
    private boolean hasSeenInitialMessage = true;

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

    public void setIsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setHasSeenInitialMessage(boolean seenMessage) {
        hasSeenInitialMessage = seenMessage;
    }

    public boolean hasSeenInitialMessage() {
        return hasSeenInitialMessage;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

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

    public void save() {
        if (userIdentifier.isEmpty())
            userIdentifier = UUID.randomUUID().toString();

        ConfigManager.saveConfig(this);
        LeaderboardService.getInstance().updateUsernameIfRequired();
    }
}
