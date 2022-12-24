package com.pixplaze.plugin.reflected;

public interface ApiProvider {
    String getProvidedClassPackage();
    String getProvidedClassName();

    default String getClassPath() {
        return String.join(".", getProvidedClassPackage(), getProvidedClassName());
    }
}
