package com.pixplaze.plugin.reflected.provider.method;

import com.pixplaze.plugin.reflected.provider.ReflectedAction;

import java.lang.reflect.InvocationTargetException;

public class CallByNameMethodAction implements ReflectedAction {
    @Override
    public void perform() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        this.getClass().getMethod("").invoke(this);
    }
}
