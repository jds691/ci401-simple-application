package com.neo.game.settings;

import com.neo.game.message.Message;
import com.neo.game.message.MessageOption;
import com.neo.game.message.MessageServiceComponent;
import com.neo.twig.Engine;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class KeyCodeSetting extends EnumSetting<KeyCode> {
    private Button keyButton;
    private final RebindHandler handler = new RebindHandler();

    @Override
    public Parent createUI() {
        HBox content = new HBox();

        Label nameLabel = new Label(getName());

        KeyCode value = getValue() == null ? getDefaultValue() : getValue();

        keyButton = new Button();
        keyButton.setText(value.name());
        keyButton.setOnAction(this::handleButtonPress);
        keyButton.getStyleClass().add("rebind-button");

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(nameLabel, keyButton);

        return content;
    }

    private void handleButtonPress(ActionEvent event) {
        Message rebindMessage = new Message(
                "Notice",
                "Press any key to rebind the current action, or 'Cancel'.",
                new MessageOption(
                        "Cancel",
                        this::unhookEventHandler
                )
        );

        Stage stage = Engine.getSceneService().getStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, handler);

        MessageServiceComponent.getInstance().addToQueue(rebindMessage);
    }

    private void unhookEventHandler(ActionEvent event) {
        Stage stage = Engine.getSceneService().getStage();
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    @Override
    public void setValue(KeyCode value) {
        super.setValue(value);
        keyButton.setText(value.name());
    }

    private class RebindHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            setValue(code);

            MessageServiceComponent.getInstance().stopCurrentMessage();

            unhookEventHandler(null);
        }
    }
}
