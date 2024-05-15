package com.neo.game.settings;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StringSetting extends Setting<String> {
    private ValidationStyle validation;

    private TextField field;

    public StringSetting() {
        this(ValidationStyle.NONE);
    }

    public StringSetting(ValidationStyle validation) {
        super();

        this.validation = validation;
    }

    @Override
    public Parent createUI() {
        HBox content = new HBox();

        Label nameLabel = new Label(getName());

        String value = getValue() == null ? getDefaultValue() : getValue();

        field = new TextField();
        field.setText(value);
        field.setOnAction(this::handleFieldUpdate);

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(nameLabel, field);

        return content;
    }

    private void handleFieldUpdate(ActionEvent event) {
        switch (validation) {
            case NONE -> setValue(field.getText());
            case NOT_EMPTY -> {
                if (field.getText().isEmpty()) {
                    field.undo();
                } else {
                    setValue(field.getText());
                }
            }
        }

    }

    public enum ValidationStyle {
        NONE,
        NOT_EMPTY
    }
}
