package com.neo.game.message;

import com.neo.twig.events.Event;
import com.neo.twig.ui.FXComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class MessageServiceUIComponent extends FXComponent {
    private Event<Object> onMessageComplete = new Event<>();

    private Label titleLabel;
    private Label messageLabel;

    private VBox messageButtonsBox;
    private Button[] messageButtons;

    @Override
    public void start() {
        super.start();

        setVisible(false);
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

    public void setTitle(String title) {
        titleLabel.setText(title.toLowerCase());
    }

    public void setText(String text) {
        messageLabel.setText(text);
    }

    public void setButtons(MessageOption... options) {
        messageButtonsBox.getChildren().clear();

        for (MessageOption option : options) {
            Button button = new Button(option.text());
            button.setUserData(option.action());
            button.setOnAction(this::buttonActionWrapper);
            button.getStyleClass().add("button");
            VBox.setVgrow(button, Priority.ALWAYS);

            messageButtonsBox.getChildren().add(button);
        }
    }

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
}