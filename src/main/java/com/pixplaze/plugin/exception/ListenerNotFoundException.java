package com.pixplaze.plugin.exception;

public class ListenerNotFoundException extends RuntimeException {
    public ListenerNotFoundException() {
        super("Listener for server tweak is not found!");
    }

    public ListenerNotFoundException(String listenerName) {
        super("Listener for server tweak %s is not found!".formatted(listenerName));
    }
}
