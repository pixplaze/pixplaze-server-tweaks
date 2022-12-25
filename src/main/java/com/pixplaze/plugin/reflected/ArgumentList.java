package com.pixplaze.plugin.reflected;

public class ArgumentList {
    private boolean strongTypeMatch = false;
    private final String wrongLengthMessage = "Count of types and arguments must be equals! Received: types[%d], arguments[%d]";
    private Class<?>[] types = null;
    private Object[] arguments = null;

    public ArgumentList strongTypeMatch(boolean strongTypeMatch) {
        this.strongTypeMatch = strongTypeMatch;
        return this;
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

    public ArgumentList split(Object ... typesAndArguments) {
        if (typesAndArguments.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Еhe number of elements in the total array of types and arguments must be the same!" +
                    "Provided:%n%s".formatted());
        }
        var countOfArguments = typesAndArguments.length / 2;
        var argumentTypes = new Class<?>[countOfArguments];
        var argumentObjects = new Object[countOfArguments];
        for (int i = 0, j = countOfArguments; i < countOfArguments; i++, j++) {
            argumentTypes[i] = (Class<?>) arguments[i];
            argumentObjects[i] = arguments[j];
        }
        this.types = argumentTypes;
        this.arguments = argumentObjects;
        return this;
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
