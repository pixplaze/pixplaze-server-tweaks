package com.pixplaze.plugin.reflected.provider;

import com.pixplaze.plugin.reflected.exceptins.ProvidedClassException;

import java.util.function.Supplier;

public abstract class AbstractReflectionProvider {
//    private ReflectedAction action = null;
    protected Exception exception = null;
    private boolean nextTry = false;

    protected void provide(ReflectedAction action) throws Exception {
        try {
            if (isSuccessful() || needRepeat()) {
                action.perform();
                exception = null;
            }
        } catch (Exception e) {
            exception = e;
            throw e;
        }
    }

    public AbstractReflectionProvider or() {
        nextTry = true;
        return this;
    }

    public void orElseThrow() {
        orElseThrow(ProvidedClassException::new);
    }

    public void orElseThrow(Supplier<Exception> exceptionSupplier) {
        var exception = exceptionSupplier.get();
        if (exception instanceof ProvidedClassException providedClassException) {
            throw providedClassException;
        } else throw new ProvidedClassException(exceptionSupplier.get());
    }

    protected boolean needRepeat() {
        return nextTry() && isFailed();
    }

    protected boolean isSuccessful() {
        return exception == null;
    }

    protected boolean isFailed() {
        return !isSuccessful();
    }

//    private boolean sameAction(ReflectedAction action) {
//        return this.action.getClass().equals(action.getClass());
//    }

    private boolean nextTry() {
        return nextTry;
    }
}
