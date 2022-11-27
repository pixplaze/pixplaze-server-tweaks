package com.pixplaze.plugin.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TweakCommandAction {
    SHOW,
    LIST,
    STATUS,
    ENABLE,
    DISABLE,
    UNDEFINED;

    public static TweakCommandAction from(String string) {
        var command = UNDEFINED;
        switch (string.toLowerCase()) {
            case "on", "enable" -> command = ENABLE;
            case "off", "disable" -> command = DISABLE;
            case "show" -> command = SHOW;
            case "status" -> command = STATUS;
            case "list" -> command = LIST;
        }
        return command;
    }

    public static List<String> available() {
        return Arrays.stream(TweakCommandAction.values())
                .filter(act -> act != TweakCommandAction.UNDEFINED)
                .map(act -> act.name().toLowerCase())
                .collect(Collectors.toList());
    }
}
