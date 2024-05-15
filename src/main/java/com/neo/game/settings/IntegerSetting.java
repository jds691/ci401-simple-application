package com.neo.game.settings;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class IntegerSetting extends Setting<Integer> {
    @Override
    public Parent createUI() {
        HBox content = new HBox();
        Label text = new Label("INVALID_UI_STATE");

        Label settingText = new Label(getName());

        //TODO: This might not work fully but I also don't think we use this so ehhhh
        //TextField inputField = new TextField(getValue().toString());

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(settingText, text);

        return text;
    }
}
