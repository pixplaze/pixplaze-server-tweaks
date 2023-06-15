package com.pixplaze.plugin.reflected.provider;

import com.pixplaze.plugin.reflected.exceptins.NoKnownProvidedClass;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ReflectedClass {

    private static final Map<String, ReflectedClass> knownReflectedClasses = new HashMap<>();
    private final String classPath;
    private Class<?> providedClass;
    private Constructor<?> providedClassConstructor;
    private Object providedClassObject;
    private Exception exception;
    private boolean isNextTry = false;

    private ReflectedClass(String classPath, Class<?> providedClass) {
        this(classPath, providedClass, null, null, null);
    }

    private ReflectedClass(String classPath, Class<?> providedClass, Constructor<?> providedClassConstructor) {
        this(classPath, providedClass, providedClassConstructor, null, null);
    }

    private ReflectedClass(String classPath, Exception exception) {
        this(classPath, null, null, null, exception);
    }

    public ReflectedClass(String classPath, Class<?> providedClass, Constructor<?> providedClassConstructor, Object providedClassObject, Exception exception) {
        this.classPath = classPath;
        this.providedClass = providedClass;
        this.providedClassConstructor = providedClassConstructor;
        this.providedClassObject = providedClassObject;
        setFail(exception);
    }

    public static ReflectedClass from(final String classPath) {
        return from(classPath, true);
    }

    private static ReflectedClass from(final String classPath, boolean findEmptyConstructor) {
        try {
            if (findEmptyConstructor) {
                var providedClass = Class.forName(classPath);
                var reflectedClass = new ReflectedClass(classPath, providedClass, providedClass.getConstructor());
                knownReflectedClasses.put(classPath, reflectedClass);
                return reflectedClass;
            } else {
                var reflectedClass = new ReflectedClass(classPath, Class.forName(classPath));
                knownReflectedClasses.put(classPath, reflectedClass);
                return reflectedClass;
            }
        } catch (NoSuchMethodException e) {
            return from(classPath, false);
        } catch (ClassNotFoundException e) {
            return new ReflectedClass(classPath, e);
        }
    }

    public static ReflectedClass find(final String className) {
        var splitClassPath = className.split("\\.");

        if (splitClassPath.length > 1) {
            return knownReflectedClasses.get(className);
        } else {
            return knownReflectedClasses.entrySet().stream()
                    .filter(entry -> {
                        var split = entry.getKey().split("\\.");
                        var name = split[split.length - 1];
                        return name.equals(className);
                    })
                    .findFirst()
                    .orElseThrow(NoKnownProvidedClass::new)
                    .getValue();
        }
    }

    public Object call(final String methodName) {

    }

    public ReflectedClass constructor(Class<?>[] arguments) {
        if (isNotAvailable()) return this;
        if (isFail() || !isNextTry()) {

        }
        try {
            providedClass.getConstructor(arguments);
            setSuccess();
            setNextTry(false);
        } catch (Exception e) {
            setFail(e);
        }
        return this;
    }

    public ReflectedClass or() {
        setNextTry(true);
        return this;
    }

    public boolean hasConstructor() {
        return providedClassConstructor != null;
    }

    public boolean isAvailable() {
        return providedClass != null;
    }

    public boolean isNotAvailable() {
        return !isAvailable();
    }

    public boolean isSuccess() {
        return exception == null;
    }

    public boolean isFail() {
        return !isSuccess();
    }

    private void setNextTry(boolean isNextTry) {
        this.isNextTry = isNextTry;
    }

    private boolean isNextTry() {
        return isNextTry;
    }

    private void setSuccess() {
        exception = null;
    }

    private void setFail(Exception exception) {
        this.exception = exception;
    }
}
