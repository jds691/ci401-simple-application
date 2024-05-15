package com.neo.game.settings;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BooleanSetting extends Setting<Boolean> {
    private CheckBox checkBox;
    @Override
    public Parent createUI() {
        HBox content = new HBox();

        Label nameLabel = new Label(getName());

        checkBox = new CheckBox();
        checkBox.setSelected(getValue());
        checkBox.setOnAction(this::handleCheckBoxAction);
        checkBox.setAllowIndeterminate(false);

        content.getChildren().addAll(nameLabel, checkBox);

        return content;
    }

    private void handleCheckBoxAction(ActionEvent event) {
        setValue(checkBox.isSelected());
    }
}
