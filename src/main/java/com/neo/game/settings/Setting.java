package com.neo.game.settings;

import com.neo.twig.logger.Logger;
import javafx.scene.Parent;

public abstract class Setting<T> {
    private String name;
    private T lastSavedValue;
    private T defaultValue;
    private T currentValue;

    private DynamicGetter<T> getter;
    private DynamicSetter<T> setter;

    private static final Logger logger = Logger.getFor(Setting.class);

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void resetToDefault() {
        assert setter != null;

        setValue(defaultValue);
        save();
    }

    public void undoChanges() {
        assert setter != null;

        setValue(lastSavedValue);
    }

    public void setValueGetter(DynamicGetter<T> getter) {
        this.getter = getter;
    }

    public T getValue() {
        assert getter != null;

        return getter.get();
    }

    public void setValueSetter(DynamicSetter<T> setter) {
        this.setter = setter;
    }

    public void setValue(T value) {
        assert setter != null;

        currentValue = value;
        setter.set(value);

        logger.logInfo(String.format("Set value '%s' for Setting '%s'", getValue().toString(), getName()));
    }

    public void save() {
        lastSavedValue = currentValue;
    }

    public boolean isSaved() {
        return lastSavedValue == currentValue;
    }

    public abstract Parent createUI();
}
