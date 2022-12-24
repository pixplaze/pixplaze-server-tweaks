package com.pixplaze.plugin.reflected.exceptins;

public class ProvidedClassException extends RuntimeException {

    public ProvidedClassException() {
        this("Exception while using API of provided class!");
    }

    public ProvidedClassException(String message) {
        super(message);
    }
}
