package com.neo.game.settings;

@FunctionalInterface
public interface DynamicSetter<T> {
    void set(T value);
}
