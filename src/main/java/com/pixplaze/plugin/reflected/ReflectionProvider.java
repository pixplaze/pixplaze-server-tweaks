package com.pixplaze.plugin.reflected;

import com.pixplaze.plugin.reflected.exceptins.ProvidedClassException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ReflectionProvider {

    private ReflectionProvider() {}

    public static Class<?> tryCreateClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ProvidedClassException("Can not load %s class-provider. The reason could be an API change"
                    .formatted(className));
        }
    }

    /**
     * Attempts to create an instance of the class with the specified arguments, with a strong type match.
     * If a child is passed to the argument whose type does not strictly match the type of the target argument,
     * an exception will be thrown.
     * @param type type (class) which instance to create;
     * @param arguments class constructor types of arguments and arguments (1/2);
     * @return an instance of class;
     */
    public static Object tryCreateObject(Class<?> type, Object ... arguments) {
        var argumentTypes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentTypes[i] = (Class<?>) arguments[i];
        }
        return tryCreateObjectByStrongTypes0(type, argumentTypes, arguments);
    }

    public static Object tryCreateObjectByStrongTypes(Class<?> type, Object ... arguments) {
        var countOfArguments = arguments.length / 2;
        var splitTypesAndObjects = splitTypesAndArguments(arguments);
        var argumentTypes = splitTypesAndObjects[0];
        var argumentObjects = splitTypesAndObjects[1];



        return tryCreateObjectByStrongTypes0(type, argumentTypes, argumentObjects);
    }

    protected static Object tryCreateObjectByStrongTypes0(Class<?> type, Class<?>[] argumentTypes, Object[] arguments) {
        var constructorDisplayName = StringUtils.toStringMethodArguments(type.getSimpleName(), argumentTypes);

        try {
//            Arrays.stream(type.getConstructors()).forEach(constructor -> {
//                plugin.getServer().sendMessage(
//                        Component.text(toStringMethodArguments(type.getSimpleName(), constructor.getParameterTypes()))
//                                .color(TextColor.color(Color.TEAL.asRGB())));
//            });
            return type.getDeclaredConstructor(argumentTypes).newInstance(arguments);
        } catch (InstantiationException e) {
            throw new ProvidedClassException("Can not create %s class by constructor %s"
                    .formatted(type.getName(), constructorDisplayName));
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public constructor %s of provided class %s"
                    .formatted(constructorDisplayName, type.getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking constructor of provided class %s. Cause %s: %s"
                    .formatted(type.getName(), e.getCause().getClass().getSimpleName(), e.getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No such constructor %s in provided class %s"
                    .formatted(constructorDisplayName, type.getName()));
        }
    }

    public static Object tryInvokeMethod(Object object, Method method, Object ... arguments) {
        var methodDisplayName = StringUtils.toStringMethodArguments(method.getName(), arguments);

        try {
            return method.invoke(object, arguments);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public method %s of provided class %s"
                    .formatted(methodDisplayName, object.getClass().getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking provided method %s. Cause %s: %s"
                    .formatted(methodDisplayName, e.getCause().getClass().getSimpleName(), e.getCause().getMessage()));
        }
    }

    public static Object tryInvokeMethod(Object object, String methodName, Object ... arguments) {
        var argumentsTypes = new Class<?>[arguments.length];
        for (var i = 0; i < arguments.length; i++) {
            argumentsTypes[i] = arguments[i].getClass();
        }
        var methodDisplayName = StringUtils.toStringMethodArguments(methodName, argumentsTypes);

        try {
            return object.getClass().getDeclaredMethod(methodName, argumentsTypes).invoke(object, arguments);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public method %s of provided class %s"
                    .formatted(methodDisplayName, object.getClass().getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking provided method %s. Cause %s: %s"
                    .formatted(methodDisplayName, e.getCause().getClass().getSimpleName(), e.getCause().getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No method %s in provided class %s"
                    .formatted(methodDisplayName, object.getClass().getName()));
        }
    }

    public static Object tryInvokeMethodByMethodArgumentTypes(Object object, Class<?>[] argumentTypes) {
        var objectClass = object.getClass();
        var objectMethods = objectClass.getMethods();
        for (var method : objectMethods) {
            var methodParameterTypes = method.getParameterTypes();
            if (method.getParameterCount() != argumentTypes.length &&
                Arrays.equals(methodParameterTypes, argumentTypes)) {
                return method;
            }
        }
        throw new ProvidedClassException("No method with parameter types %s in provided class %s"
                .formatted(Arrays.toString(argumentTypes), objectClass.getName()));
    }

    public static Object tryGetFieldValue(Object object, String fieldName) {
        try {
            return object.getClass().getField(fieldName).get(object);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public field %s of provided class %s"
                    .formatted(fieldName, object.getClass().getName()));
        } catch (NoSuchFieldException e) {
            throw new ProvidedClassException("No field %s in provided class %s"
                    .formatted(fieldName, object.getClass().getName()));
        }
    }

    public static Object tryGetFieldValueByFieldType(Object object, Class<?> type) {
        var fields = object.getClass().getFields();

        try {
            for (var field : fields) {
                if (type.equals(field.getType())) {
                    return field.get(object);
                }
            }
        } catch (Exception e) {
            throw new ProvidedClassException("No field with type %s in provided class %s"
                    .formatted(type.getName(), object.getClass().getName()));
        }

        throw new ProvidedClassException("No field with type %s in provided class %s"
                .formatted(type.getName(), object.getClass().getName()));
    }

    public static Object tryGetEnumValueObject(Class<?> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(item -> {
                    try {
                        return enumClass.getMethod("name").invoke(item).toString().equals(name);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
//                .map(enumType -> {
//                    try {
//                        return enumType.getClass().cast(enumClass.getConstructor().newInstance());
//                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
//                             InstantiationException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
                .orElseThrow(() -> new ProvidedClassException("Can not get enum value object %s from provided class %s"
                        .formatted(name, enumClass.getName())));
    }
}
