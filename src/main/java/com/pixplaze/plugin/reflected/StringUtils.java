package com.pixplaze.plugin.reflected;

import java.util.Arrays;

public class StringUtils {

    public static String toStringMethodArguments(String methodName, Object[] arguments) {
        return toStringMethodArguments(methodName, arguments, false);
    }

    public static String toStringMethodArguments(String methodName, Object[] argumentTypes, boolean verbose) {
        return Arrays.stream(argumentTypes)
                .map(obj -> {
                    if (verbose) {
                        return obj.getClass().getTypeName();
                    } else {
                        var classTypeName = obj.getClass().getSimpleName();
                        var splitClassTypeName = classTypeName.split("\\.");
                        return splitClassTypeName[splitClassTypeName.length - 1];
                    }
                })
                .reduce((curr, next) -> curr + ", " + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse("%s()".formatted(methodName));
    }

    public static String toStringMethodArguments(String methodName, Class<?>[] argumentTypes) {
        return toStringMethodArguments(methodName, argumentTypes, false);
    }

    public static String toStringMethodArguments(String methodName, Class<?>[] argumentTypes, boolean verbose) {
        return Arrays.stream(argumentTypes)
                .map(classType -> {
                    if (verbose) {
                        return classType.getTypeName();
                    } else {
                        var classTypeName = classType.getSimpleName();
                        var splitClassTypeName = classTypeName.split("\\.");
                        return splitClassTypeName[splitClassTypeName.length - 1];
                    }
                })
                .reduce((curr, next) -> curr + ", " + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse("%s()".formatted(methodName));
    }

    public static String toStringTypes(Object ... objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .reduce((curr, next) -> String.join(", "))
                .orElse("");
    }

    public static String toStringTypesFullNames(Object ... objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(Class::getTypeName)
                .reduce((curr, next) -> String.join(", "))
                .orElse("");
    }
}
