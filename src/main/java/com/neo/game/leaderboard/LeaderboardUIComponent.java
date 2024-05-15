package com.neo.game.leaderboard;

import com.neo.twig.ui.FXComponent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.Arrays;

/**
 * Represents the user interface for the leaderboard score tables
 */
public class LeaderboardUIComponent extends FXComponent {
    private LeaderboardService leaderboard;
    private ObservableList<LeaderboardScore> scores = FXCollections.observableArrayList();

    @Override
    public void start() {
        super.start();

        leaderboard = LeaderboardService.getInstance();
        setVisible(false);
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        Button backButton = new Button("Back");
        backButton.setOnAction(this::handleBackButton);

        Label titleLabel = new Label("leaderboard");
        titleLabel.getStyleClass().add("title-label");

        TableColumn<LeaderboardScore, String> userName = new TableColumn("Username");
        userName.setCellValueFactory(
                new PropertyValueFactory<>("userName")
        );
        TableColumn<LeaderboardScore, Integer> score = new TableColumn("Score");
        score.setCellValueFactory(
                new PropertyValueFactory<>("score")
        );
        TableColumn<LeaderboardScore, String> gameVersion = new TableColumn("Game Version");
        gameVersion.setCellValueFactory(
                new PropertyValueFactory<>("gameVersion")
        );

        TableView<LeaderboardScore> table = new TableView<>();
        table.setItems(scores);
        table.getColumns().addAll(userName, score, gameVersion);

        root.setTop(titleLabel);
        root.setBottom(backButton);
        root.setCenter(table);

        return root;
    }

    private void handleBackButton(ActionEvent event) {
        setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible)
            updateScores();
    }

    private void updateScores() {
        LeaderboardScore[] fetchedScores = leaderboard.getAllScores();

        if (fetchedScores != null) {
            scores.clear();

            scores.addAll(Arrays.asList(fetchedScores));
        }
    }
}
