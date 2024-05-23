package com.neo.game.settings;

import com.neo.game.leaderboard.LeaderboardService;
import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Represents a number of pre-defined {@link SettingCategory} objects
 */
public class SettingsUIComponent extends FXComponent {
    private SettingCategory[] categories;
    private ArrayList<Button> tabButtons = new ArrayList<>();
    private int currentTab = -1;
    private ScrollPane categoryRoot;

    private Label noContentLabel;

    private Button saveButton;
    private Button undoButton;
    private Button resetToDefaultButton;

    public SettingsUIComponent() {
        categories = new SettingCategory[] {
                DisplaySettings.getInstance().getSettingsCategory(),
                KeyBindSettings.getInstance().getSettingsCategory(),
                LeaderboardService.getInstance().getSettings().getSettingsCategory()
        };
    }

    @Override
    public void start() {
        super.start();

        setVisible(false);
    }

    @Override
    public Parent generateFXScene() {
        BorderPane root = new BorderPane();
        root.setId("root");

        VBox topBar = new VBox();
        topBar.setId("top-bar");

        Label titleLabel = new Label("settings");
        titleLabel.getStyleClass().add("title-label");

        HBox tabBar = new HBox();
        tabBar.setId("tab-bar");

        topBar.getChildren().addAll(titleLabel, tabBar);

        for (int i = 0; i < categories.length; i++) {
            SettingCategory category = categories[i];

            Button categoryEntry = new Button(category.getName());
            categoryEntry.setUserData(i);
            categoryEntry.setOnAction(this::changeCategoryView);
            categoryEntry.getStyleClass().add("tab-entry");
            HBox.setHgrow(categoryEntry, Priority.ALWAYS);
            tabButtons.add(categoryEntry);
            tabBar.getChildren().add(categoryEntry);
        }

        categoryRoot = new ScrollPane();
        categoryRoot.setId("content-root");
        VBox categoryControls = new VBox();
        categoryControls.setId("content-controls");

        categoryRoot.setContent(categoryControls);

        noContentLabel = new Label("Select a category.");
        categoryControls.getChildren().add(noContentLabel);

        HBox bottomBar = new HBox();
        bottomBar.setId("bottom-bar");

        Button backButton = new Button("Back");
        backButton.setOnAction(this::onBackButton);

        saveButton = new Button("Save");
        saveButton.setOnAction(this::onSaveButton);
        //saveButton.setDisable(true);

        undoButton = new Button("Undo Changes");
        undoButton.setOnAction(this::onUndoButton);
        //undoButton.setDisable(true);

        resetToDefaultButton = new Button("Reset to Defaults");
        resetToDefaultButton.setOnAction(this::onResetToDefaultButton);
        //resetToDefaultButton.setDisable(true);

        bottomBar.getChildren().addAll(backButton, saveButton, undoButton, resetToDefaultButton);

        root.setTop(topBar);
        root.setBottom(bottomBar);
        root.setCenter(categoryRoot);

        return root;
    }

    private void changeCategoryView(ActionEvent event) {
        if (currentTab != -1)
            tabButtons.get(currentTab).getStyleClass().remove("active");

        if (currentTab != -1 && !categories[currentTab].isSaved()) {
            MessageServiceComponent.getInstance().addToQueue(
                    new Message(
                            "Notice",
                            "You have unsaved changes, would you like to save them?",
                            new MessageOption(
                                    "Yes",
                                    this::handleUnsavedOnChange_Yes
                            ),
                            new MessageOption(
                                    "No",
                                    this::handleUnsavedOnChange_No
                            )
                    )
            );
        } else {
            Button categoryEntry = (Button) event.getSource();
            currentTab = (int) categoryEntry.getUserData();

            categoryEntry.getStyleClass().add("active");

            handleCategoryChange();
        }
    }

    private void handleCategoryChange() {
        // Draw the rest of the fucking UI
        SettingCategory selectedCategory = categories[currentTab];
        selectedCategory.setSuppressTitleDisplay(true);
        VBox categoryControls = (VBox) selectedCategory.createUI();
        categoryControls.setId("content-controls");

        categoryRoot.setContent(categoryControls);
    }

    private void handleUnsavedOnChange_Yes(ActionEvent event) {
        onSaveButton(event);

        handleCategoryChange();
    }

    private void handleUnsavedOnChange_No(ActionEvent event) {
        handleUndoChanges(event);

        handleCategoryChange();
    }

    private void onBackButton(ActionEvent event) {
        if (currentTab != -1 && !categories[currentTab].isSaved()) {
            MessageServiceComponent.getInstance().addToQueue(
                    new Message(
                            "Notice",
                            "You have unsaved changes, would you like to save them?",
                            new MessageOption(
                                    "Yes",
                                    this::handleUnsavedOnExit_Yes
                            ),
                            new MessageOption(
                                    "No",
                                    this::handleUnsavedOnExit_No
                            )
                    )
            );
        } else {
            setVisible(false);
        }
    }

    private void handleUnsavedOnExit_Yes(ActionEvent event) {
        onSaveButton(event);
        setVisible(false);
    }

    private void handleUnsavedOnExit_No(ActionEvent event) {
        handleUndoChanges(event);
        setVisible(false);
    }

    private void onSaveButton(ActionEvent event) {
        if (currentTab == -1)
            return;

        SettingCategory category = categories[currentTab];
        category.save();

        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "Notice",
                        "Your changes have been saved.",
                        new MessageOption(
                                "OK",
                                null
                        )
                )
        );
    }

    private void onUndoButton(ActionEvent event) {
        if (currentTab == -1)
            return;

        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "Notice",
                        "Are you sure you want to undo your changes?",
                        new MessageOption(
                                "Yes",
                                this::handleUndoChanges
                        ),
                        new MessageOption(
                                "No",
                                null
                        )
                )
        );
    }

    private void handleUndoChanges(ActionEvent event) {
        if (currentTab != -1)
            categories[currentTab].undoChanges();

        categories[currentTab].save();
    }

    private void onResetToDefaultButton(ActionEvent event) {
        if (currentTab == -1)
            return;

        MessageServiceComponent.getInstance().addToQueue(
                new Message(
                        "Warning",
                        "You are about to reset all settings to their default values, this cannot be undone. Are you sure you want to continue?",
                        new MessageOption(
                                "Yes",
                                this::handleResetToDefault
                        ),
                        new MessageOption(
                                "No",
                                null
                        )
                )
        );
    }

    private void handleResetToDefault(ActionEvent event) {
        if (currentTab != -1)
            categories[currentTab].resetToDefault();

        categories[currentTab].save();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (!visible) {
            if (currentTab != -1)
                tabButtons.get(currentTab).getStyleClass().remove("active");

            currentTab = -1;

            VBox placeholder = new VBox();
            placeholder.getChildren().add(noContentLabel);
            placeholder.setId("content-controls");

            categoryRoot.setContent(placeholder);
        }
    }
}
