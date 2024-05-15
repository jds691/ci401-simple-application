package com.neo.game.settings;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
    private HashMap<Integer, String> enumMapping = new HashMap<>();
    private MenuButton dropdown;
    @Override
    public Parent createUI() {
        HBox content = new HBox();

        Label title = new Label(getName());
        dropdown = new MenuButton();
        ToggleGroup toggleGroup = new ToggleGroup();


        Class<T> enumType = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        T value = getValue() == null ? getDefaultValue() : getValue();

        for (T constant : enumType.getEnumConstants()) {
            RadioMenuItem menuItem = new RadioMenuItem();
            menuItem.setText(constant.name());
            menuItem.setUserData(constant);
            menuItem.setToggleGroup(toggleGroup);
            menuItem.setOnAction(this::handleEnumSelection);

            if (constant == value)
                menuItem.setSelected(true);

            dropdown.getItems().add(menuItem);

            enumMapping.put(constant.ordinal(), constant.name());
        }

        dropdown.setText(value.name());

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(title, dropdown);

        return content;
    }

    private void handleEnumSelection(ActionEvent event) {
        RadioMenuItem item = (RadioMenuItem) event.getSource();
        T constantData = (T) item.getUserData();

        item.setSelected(true);
        dropdown.setText(constantData.name());
        setValue(constantData);
    }
}
