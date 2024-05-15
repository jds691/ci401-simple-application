package com.neo.game.settings;

@FunctionalInterface
public interface DynamicGetter<T> {
    T get();
}
