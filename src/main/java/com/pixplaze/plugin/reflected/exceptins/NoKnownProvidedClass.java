package com.pixplaze.plugin.reflected.exceptins;

public class NoKnownProvidedClass extends ProvidedClassException {
    public NoKnownProvidedClass() {
        super("Can not find class in known provided classes!");
    }
}
