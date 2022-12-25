package com.pixplaze.plugin.reflected;

import static com.pixplaze.plugin.reflected.StringUtils.toStringTypes;

public class ArgumentList {
    private boolean strongTypeMatch = false;
    private final String wrongLengthMessage = "Count of types and arguments must be equals! Received: types[%d], arguments[%d]";
    private Class<?>[] types = null;
    private Object[] arguments = null;

    public ArgumentList() {}

    private ArgumentList(Class<?>[] types, Object[] arguments) {
        this.types = types;
        this.arguments = arguments;
    }

    public ArgumentList put(Class<?>[] types, Object[] arguments) {
        this.types = types;
        this.arguments = arguments;
        if (!isLengthsMatch()) {
            this.types = null;
            this.arguments = null;
            throw new IllegalArgumentException(
                    wrongLengthMessage.formatted(types.length, arguments.length));
        }
        return this;
    }

    public static ArgumentList split(Object ... typesAndArguments) {
        if (typesAndArguments.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Еhe number of elements in the total array of types and arguments must be the same!" +
                    "Provided:%n%s".formatted(toStringTypes(typesAndArguments)));
        }
        var countOfArguments = typesAndArguments.length / 2;
        var argumentTypes = new Class<?>[countOfArguments];
        var argumentObjects = new Object[countOfArguments];
        for (int i = 0, j = countOfArguments; i < countOfArguments; i++, j++) {
            argumentTypes[i] = (Class<?>) typesAndArguments[i];
            argumentObjects[i] = typesAndArguments[j];
        }
        return new ArgumentList(argumentTypes, argumentObjects);
    }

    public ArgumentList putTypes(Class<?> ... types) {
        this.types = types;
        if (isCompleted() && !isLengthsMatch()) {
            this.types = null;
            throw new IllegalArgumentException(
                    wrongLengthMessage.formatted(types.length, arguments.length));
        }
        return this;
    }

    public ArgumentList putArguments(Object ... arguments) {
        this.arguments = arguments;
        if (isCompleted() && !isLengthsMatch()) {
            this.arguments = null;
            throw new IllegalArgumentException(
                    wrongLengthMessage.formatted(types.length, arguments.length));
        }
        return this;
    }

    public Class<?>[] getTypes() {
        if (getTypesCount() == 0) {
            return new Class<?>[0];
        }
        return this.types;
    }

    public Object[] getArguments() {
        if (getArgumentsCount() == 0) {
            return new Object[0];
        }
        return this.arguments;
    }

    public boolean isCompleted() {
        return types != null && arguments != null;
    }

    public boolean isDirty() {
        return types != null || arguments != null;
    }

    public int count() {
        return isCompleted() ? getArgumentsCount() : 0;
    }

    private boolean isLengthsMatch() {
        if (types == null || arguments == null) return false;
        return types.length == arguments.length;
    }

    private int getTypesCount() {
        return types == null ?
                0 : types.length;
    }

    private int getArgumentsCount() {
        return arguments == null ?
                0 : arguments.length;
    }
}
