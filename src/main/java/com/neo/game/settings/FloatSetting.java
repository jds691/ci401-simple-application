package com.neo.game.settings;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class FloatSetting extends Setting<Float> {
    private Configuration config;
    private Slider adjustmentSlider;

    public FloatSetting() {
        this(Configuration.DEFAULT);
    }

    public FloatSetting(Configuration configuration) {
        this.config = configuration;
    }

    @Override
    public Parent createUI() {
        HBox content = new HBox();

        Label settingText = new Label(getName());

        float value = getValue() == null ? getDefaultValue() : getValue();

        adjustmentSlider = new Slider();
        adjustmentSlider.setValue(value);
        adjustmentSlider.setBlockIncrement(config.incrementStep());
        adjustmentSlider.setMin(config.min());
        adjustmentSlider.setMax(config.max());

        adjustmentSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleFieldUpdate());
        //TextField inputField = new TextField(getValue().toString());

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(settingText, adjustmentSlider);

        return content;
    }

    private void handleFieldUpdate() {
        setValue((float) adjustmentSlider.getValue());
    }

    public record Configuration(
            float min,
            float max,
            float incrementStep
    ) {
        public static final Configuration DEFAULT = new Configuration(
                0f,
                1f,
                0.1f
        );
    }
}
