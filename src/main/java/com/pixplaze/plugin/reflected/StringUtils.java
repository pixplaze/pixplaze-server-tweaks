package com.pixplaze.plugin.reflected;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        return Arrays.stream(argumentTypes)
                .map(StringUtils::toStringClassType)
                .reduce((curr, next) -> curr + ", " + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse("%s()".formatted(methodName));
    }

    public static String toStringMethodArgumentsFullName(String methodName, Class<?>[] argumentTypes) {
        return Arrays.stream(argumentTypes)
                .map(StringUtils::toStringClassTypeFullName)
                .reduce((curr, next) -> curr + ", " + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse("%s()".formatted(methodName));
    }

    public static String toStringTypes(Object ... objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(StringUtils::toStringClassType)
                .collect(Collectors.joining(", "));
    }

    public static String toStringTypesFullNames(Object ... objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(StringUtils::toStringClassTypeFullName)
                .collect(Collectors.joining(", "));
    }

    public static String toStringClassType(Class<?> type) {
        var classTypeName = type.toGenericString();
        var splitClassTypeName = classTypeName.split("\\.");
        return splitClassTypeName[splitClassTypeName.length - 1];
    }

    public static String toStringClassTypeFullName(Class<?> type) {
        return type.toGenericString();
    }
}
