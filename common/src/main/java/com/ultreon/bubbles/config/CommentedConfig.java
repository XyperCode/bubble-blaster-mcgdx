package com.ultreon.bubbles.config;

public interface CommentedConfig {

    <T> T get(String path);

    <T> void set(String path, T value);

    <T extends Enum<T>> T getEnum(String path, Class<T> declaringClass);

    void setComment(String path, String comment);
}
