package com.neo.game.leaderboard;

import com.neo.twig.config.ConfigManager;

public class LeaderboardService {
    private static LeaderboardService instance;
    private LeaderboardSettings settings;

    private LeaderboardService() {
        settings = new LeaderboardSettings();

        ConfigManager.loadConfig(settings);
    }

    public static LeaderboardService getInstance() {
        if (instance == null) {
            instance = new LeaderboardService();
        }

        return instance;
    }

    public boolean isLeaderboardEnabled() {
        return settings.isEnabled();
    }

    public void setLeaderboardEnabled(boolean enabled) {
        settings.setIsEnabled(enabled);
    }

    public void saveSettings() {
        ConfigManager.saveConfig(settings);
    }
}
