package com.neo.game.message;

import com.neo.game.audio.UISoundPlayer;
import com.neo.twig.events.Event;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Represents the UI used to draw UI messages
 */
public class MessageServiceUIComponent extends FXComponent {
    private Event<Object> onMessageComplete = new Event<>();

    private Label titleLabel;
    private Label messageLabel;

    private VBox messageButtonsBox;
    private Button[] messageButtons;

    private String showSfxKey = "UI_message_show";
    private String hideSfxKey = "UI_message_hide";

    private UISoundPlayer showSfx;
    private UISoundPlayer hideSfx;

    @Override
    public void start() {
        super.start();

        setVisible(false);

        // Initialise required sounds
        showSfx = new UISoundPlayer(showSfxKey);
        hideSfx = new UISoundPlayer(hideSfxKey);
    }

    @Override
    public Parent generateFXScene() {
        StackPane root = new StackPane();
        root.setId("root");

        VBox messageContents = new VBox();
        messageContents.setId("contents");
        messageContents.setAlignment(Pos.CENTER);

        titleLabel = new Label("error");
        titleLabel.getStyleClass().add("title-label");
        VBox.setVgrow(titleLabel, Priority.ALWAYS);

        messageLabel = new Label("The contents of this message have not been set.");
        messageLabel.getStyleClass().add("label");
        VBox.setVgrow(messageLabel, Priority.ALWAYS);

        messageLabel.setWrapText(true);

        messageButtonsBox = new VBox();
        messageButtonsBox.setId("button-container");
        VBox.setVgrow(messageButtonsBox, Priority.ALWAYS);

        MessageOption placeholder = new MessageOption("OK", null);

        messageContents.getChildren().addAll(titleLabel, messageLabel, messageButtonsBox);
        setButtons(placeholder);

        root.getChildren().add(messageContents);

        return root;
    }

    /**
     * Sets the title of the message UI
     *
     * @param title Message title
     */
    public void setTitle(String title) {
        titleLabel.setText(title.toLowerCase());
    }

    /**
     * Sets the text body of the message UI
     *
     * @param text Message body
     */
    public void setText(String text) {
        messageLabel.setText(text);
    }

    /**
     * Sets the buttons/potential options the user can pick
     *
     * @param options Button options
     */
    public void setButtons(MessageOption... options) {
        messageButtonsBox.getChildren().clear();

        for (MessageOption option : options) {
            Button button = new Button(option.text());
            button.setUserData(option.action());
            button.setOnAction(this::buttonActionWrapper);
            VBox.setVgrow(button, Priority.ALWAYS);

            messageButtonsBox.getChildren().add(button);
        }
    }

    /**
     * Gets an event that can be listened to for when the user has selected an option
     *
     * @return Event to listen to
     */
    public Event<Object> getOnMessageCompleteEvent() {
        return onMessageComplete;
    }

    private void buttonActionWrapper(ActionEvent event) {
        onMessageComplete.emit(null);

        Button button = (Button) event.getSource();
        Object userData = button.getUserData();

        if (userData != null) {
            EventHandler<ActionEvent> realHandler = (EventHandler<ActionEvent>) userData;
            realHandler.handle(event);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && showSfx != null)
            showSfx.play();
        else if (!visible && hideSfx != null)
            hideSfx.play();
    }
}
