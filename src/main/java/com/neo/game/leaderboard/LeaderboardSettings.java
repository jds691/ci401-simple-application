package com.neo.game.leaderboard;

import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.config.ConfigProperty;

@Config(name = "leaderboard")
public class LeaderboardSettings {
    @ConfigProperty(section = "Connection")
    @ForceSerialize
    private boolean enabled;

    public void setIsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
