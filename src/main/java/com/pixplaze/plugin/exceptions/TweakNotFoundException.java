package com.pixplaze.plugin.exceptions;

public class TweakNotFoundException extends RuntimeException {
    public TweakNotFoundException() {
        this("Server tweak is not found!");
    }

    public TweakNotFoundException(String tweakName) {
        super("Server tweak '%s' is not found!".formatted(tweakName));
    }
}
