package com.neo.game.leaderboard;

import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.twig.Engine;
import com.neo.twig.logger.Logger;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigScope;

import java.sql.*;
import java.util.ArrayList;

public class LeaderboardService {
    private static final Message CHANGE_IN_SETTINGS = new Message(
            "Notice",
            "Not a problem. If you change your mind, you can enable it in settings.",
            new MessageOption(
                    "OK",
                    null
            )
    );

    private static final Message LEADERBOARD_INTRO = new Message(
            "Notice",
            "This game supports an opt-in leaderboard system that allows you to view and share scores. Would you like to enable it?",
            new MessageOption(
                    "Yes",
                    (event) -> LeaderboardService.getInstance().getSettings().setIsEnabled(true)
            ),
            new MessageOption(
                    "No",
                    (event) -> MessageServiceComponent.getInstance().addToQueue(CHANGE_IN_SETTINGS)
            )
    );

    private static final Message LEADERBOARD_INTRO_GAME = new Message(
            "Notice",
            "Congrats on beating your first game! This game supports an opt-in leaderboard system that allows you to view and share scores. Would you like to enable it?",
            new MessageOption(
                    "Yes",
                    (event) -> LeaderboardService.getInstance().getSettings().setIsEnabled(true)
            ),
            new MessageOption(
                    "No",
                    (event) -> MessageServiceComponent.getInstance().addToQueue(CHANGE_IN_SETTINGS)
            )
    );

    private static final Logger logger = Logger.getFor(LeaderboardService.class);

    private static LeaderboardService instance;

    private LeaderboardSettings settings;

    private boolean driverLoaded;
    private boolean canLoadDriver;
    private boolean initialisedConnection;

    private Connection serverConnection;

    private LeaderboardService() {
        canLoadDriver = true;
        driverLoaded = false;
        settings = new LeaderboardSettings();
        ConfigManager.saveConfig(settings, ConfigScope.Engine);

        ConfigManager.loadConfig(settings);
        settings.setPreviousUsername(settings.getUsername());
    }

    public static LeaderboardService getInstance() {
        if (instance == null) {
            instance = new LeaderboardService();
        }

        return instance;
    }

    private void initialiseConnection() {
        if (canLoadDriver && !driverLoaded) {
            loadSQLDriver();
        }

        if (!canLoadDriver)
            return;

        try {
            serverConnection = DriverManager.getConnection(
                    LeaderboardConnectionData.URL,
                    LeaderboardConnectionData.USERNAME,
                    LeaderboardConnectionData.PASSWORD
            );
            initialisedConnection = true;

        } catch (SQLException e) {
            canLoadDriver = false;
            initialisedConnection = false;
            logger.logError("Failed to initialise the leaderboard connection...");
            e.printStackTrace();
        }
    }

    public void uploadScore(int score) {
        if (score == 0)
            return;

        if (!canLoadDriver)
            return;

        if (!initialisedConnection)
            initialiseConnection();

        if (!initialisedConnection)
            return;

        try {
            PreparedStatement statement;
            int lastScore = checkUserEntryExists(settings.getUsername());
            if (lastScore == -1) {
                String statementText =
                        "INSERT INTO scores " +
                        "(username, score, versionInfo, userIdentifier) " +
                        "VALUES(?, ?, ?, ?)";
                statement = serverConnection.prepareStatement(statementText);
                statement.setString(1, settings.getUsername());
                statement.setInt(2, score);
                statement.setString(3, Engine.getConfig().appConfig().version);
                statement.setString(4, settings.getUserIdentifier());
                statement.execute();
            } else if (score >= lastScore) {
                String statementText =
                        "UPDATE scores " +
                        "SET scores.score = ?, scores.versionInfo = ?" +
                        "WHERE userIdentifier = ?";
                statement = serverConnection.prepareStatement(statementText);
                statement.setInt(1, score);
                statement.setString(2, Engine.getConfig().appConfig().version);
                statement.setString(3, settings.getUserIdentifier());
                int recordsAffected = statement.executeUpdate();

                if (recordsAffected > 1)
                    logger.logWarning(String.format("More records affected (%d) by update than expected (1)", recordsAffected));
            }

            logger.logInfo("Uploaded score to server");
        } catch (SQLException e) {
            logger.logError("Failed to update score records");
        }
    }

    public LeaderboardScore[] getAllScores() {
        if (!canLoadDriver)
            return null;

        if (!initialisedConnection)
            initialiseConnection();

        if (!initialisedConnection)
            return null;

        String statementText =
                "SELECT (username, score, versionInfo) FROM scores ORDER BY score";
        try {
            PreparedStatement statement = serverConnection.prepareStatement(statementText);
            ResultSet results = statement.executeQuery();

            ArrayList<LeaderboardScore> scores = new ArrayList<>();
            while (results.next()) {
                String userName = results.getString(1);
                int score = results.getInt(2);
                String gameVersion = results.getString(3);

                LeaderboardScore lScore = new LeaderboardScore(userName, score, gameVersion);
                scores.add(lScore);
            }

            return scores.toArray(new LeaderboardScore[0]);
        } catch (SQLException e) {
            logger.logError("Unable to obtain scores from server");
            return null;
        }
    }

    private int checkUserEntryExists(String username) {
        int score = -1;

        try {
            String statementText = "SELECT scores.score FROM scores WHERE username = ?";
            PreparedStatement statement = serverConnection.prepareStatement(statementText);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                score = results.getInt(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return score;
    }

    public void updateUsernameIfRequired() {
        if (!canLoadDriver || !settings.isEnabled())
            return;

        if (!initialisedConnection)
            initialiseConnection();

        if (!initialisedConnection)
            return;

        String userIdentifier = settings.getUserIdentifier();

        PreparedStatement statement;

        try {
            String statementText =
                    "UPDATE scores " +
                            "SET scores.username = ?" +
                            "WHERE userIdentifier = ?";
            statement = serverConnection.prepareStatement(statementText);
            statement.setString(1, settings.getUsername());
            statement.setString(2, userIdentifier);
            int recordsAffected = statement.executeUpdate();

            logger.logInfo(String.format("Updated %d score record(s)", recordsAffected));

            settings.setPreviousUsername(settings.getUsername());
        } catch (SQLException e) {

        }
    }

    private void loadSQLDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            driverLoaded = true;
        } catch (Exception ex) {
            canLoadDriver = false;
        }
    }

    public void playIntro(boolean isInGame) {
        MessageServiceComponent.getInstance().addToQueue(isInGame ? LEADERBOARD_INTRO_GAME : LEADERBOARD_INTRO);
        settings.setHasSeenInitialMessage(true);
        settings.save();
    }

    public LeaderboardSettings getSettings() {
        return settings;
    }

    public void shutdown() {
        if (serverConnection == null)
            return;

        try {
            serverConnection.close();
        } catch (SQLException ignored) {

        }
        serverConnection = null;
    }
}
