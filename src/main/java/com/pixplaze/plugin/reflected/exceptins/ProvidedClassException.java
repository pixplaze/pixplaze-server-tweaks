package com.pixplaze.plugin.reflected.exceptins;

public class ProvidedClassException extends RuntimeException {

    public ProvidedClassException() {
        super("Exception while using API of provided class!");
    }

    public ProvidedClassException(String message) {
        super(message);
    }

    public ProvidedClassException(Throwable cause) {
        super(cause);
    }

    public ProvidedClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
