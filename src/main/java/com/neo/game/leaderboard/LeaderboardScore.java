package com.neo.game.leaderboard;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Contains the relevant score data retrieved from the database
 */
public class LeaderboardScore {
    private final StringProperty userName;
    private final IntegerProperty score;
    private final StringProperty gameVersion;

    public LeaderboardScore(String username, int score, String gameVersion) {
        this.userName = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
        this.gameVersion = new SimpleStringProperty(gameVersion);
    }

    /**
     * Gets the username associated with the score
     *
     * @return Username
     */
    public String getUserName() {
        return userName.get();
    }

    /**
     * Gets the numeric score value
     *
     * @return Score
     */
    public int getScore() {
        return score.get();
    }

    /**
     * The version of the game this score was recorded on
     *
     * @return The version of the game this score was recorded on
     */
    public String getGameVersion() {
        return gameVersion.get();
    }
}
