package com.pixplaze.plugin.reflected.provider.method;

import com.pixplaze.plugin.reflected.ReflectionUtils;
import com.pixplaze.plugin.reflected.provider.AbstractReflectionProvider;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ReflectedMethod extends AbstractReflectionProvider {

    private Object providedObject;

    public Optional<Object> call(final String methodName, final Object[] arguments) {
        var methodResultValue = new AtomicReference<>();
        try {
//            provide(() -> {
//                methodResultValue.set(providedObject.getClass().getMethod(methodName));
//            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (isSuccessful()) return Optional.ofNullable(methodResultValue.get());

//        return Optional.ofNullable(ReflectionUtils.tryInvokeMethodByName(providedObject, methodName, arguments));

    }

    public Optional<Object> call(final Class<?>[] argumentTypes, final Object[] arguments) {
        provide(() -> {
            providedObject.getClass().getMethod()
        })
        return Optional.ofNullable(ReflectionUtils.tryInvokeMethodByArguments(providedObject, argumentTypes, arguments));
    }

    Object value() {
        return value;
    }
}
