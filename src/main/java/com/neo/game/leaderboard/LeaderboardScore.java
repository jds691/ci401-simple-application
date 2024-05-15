package com.neo.game.leaderboard;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LeaderboardScore {
    private final StringProperty userName;
    private final IntegerProperty score;
    private final StringProperty gameVersion;

    public LeaderboardScore(String username, int score, String gameVersion) {
        this.userName = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
        this.gameVersion = new SimpleStringProperty(gameVersion);
    }

    public String getUserName() {
        return userName.get();
    }

    public int getScore() {
        return score.get();
    }

    public String getGameVersion() {
        return gameVersion.get();
    }
}
