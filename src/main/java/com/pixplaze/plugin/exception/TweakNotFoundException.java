package com.pixplaze.plugin.exception;

public class TweakNotFoundException extends RuntimeException {
    public TweakNotFoundException() {
        this("Server tweak not found!");
    }

    public TweakNotFoundException(String tweakName) {
        super("Server tweak '%s' is not found!".formatted(tweakName));
    }
}
