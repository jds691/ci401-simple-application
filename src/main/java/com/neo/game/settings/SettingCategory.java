package com.neo.game.settings;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a collection of settings
 */
public class SettingCategory extends Setting<Setting[]> {
    private SettingsCategorySaver saver;
    private ArrayList<Setting> settings = new ArrayList<>();

    private boolean suppressTitleDisplay = false;

    /**
     * Settings to add to this category
     *
     * @param settings Settings to add
     */
    public void addChildren(Setting... settings) {
        Collections.addAll(this.settings, settings);
    }

    //Overriding implementations
    /**
     * Unsupported by SettingCategory
     */
    @Override
    public void setDefaultValue(Setting[] defaultValue) {
    }

    /**
     * Unsupported by SettingCategory
     */
    @Override
    public void setValueGetter(DynamicGetter<Setting[]> getter) {
    }

    /**
     * Unsupported by SettingCategory
     */
    @Override
    public void setValueSetter(DynamicSetter<Setting[]> setter) {
    }

    public void setSaveAction(SettingsCategorySaver saver) {
        this.saver = saver;
    }

    /**
     * Sets whether the VBox for this category should be drawn with its name or not.
     *
     * <p>
     *     It is recommended to suppress title display when a category is drawn as the root of the settings UI.
     * </p>
     *
     * @param suppress If the title should be hidden or not.
     */
    public void setSuppressTitleDisplay(boolean suppress) {
        this.suppressTitleDisplay = suppress;
    }

    @Override
    public void save() {
        for (Setting setting : settings) {
            setting.save();
        }

        if (saver != null)
            saver.save();
    }

    @Override
    public boolean isSaved() {
        for (Setting setting : settings) {
            if (!setting.isSaved())
                return false;
        }

        return true;
    }

    /**
     * Draws the category UI, and it's children
     *
     * @return A VBox control containing all of it's children's UI
     */
    @Override
    public Parent createUI() {
        VBox contents = new VBox();

        if (!suppressTitleDisplay) {
            Label titleLabel = new Label(getName());
            contents.getChildren().add(titleLabel);
            titleLabel.getStyleClass().add("header-label");
        }

        for (Setting setting : settings) {
            contents.getChildren().add(setting.createUI());
        }

        return contents;
    }

    @Override
    public void resetToDefault() {
        for (Setting setting : settings) {
            setting.resetToDefault();
        }
    }

    @Override
    public void undoChanges() {
        for (Setting setting : settings) {
            setting.undoChanges();
        }
    }
}
