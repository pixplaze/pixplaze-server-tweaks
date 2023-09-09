package com.pixplaze.plugin.util;

public class TweakUtils {

    /**
     * Преобразует логическое значение boolean в строковое представление
     * {@code true = ["enabled" | "on"], false = ["disabled" | "off"].}
     * @param status статус твика (true - включен, false - выключен);
     * @return строковое предстваление статуса твика.
     */
    public static String getStatusLabel(boolean status) {
        return status ? "enabled" : "disabled";
    }

    public static boolean booleanFromStatus(final String status) {
        boolean result;
        switch (status.toLowerCase()) {
            case "on", "enable" -> result = true;
            case "off", "disable" -> result = false;
            default -> throw new RuntimeException(
                    "Illegal value for tweak status: \"%s\"! Expected [\"on\" | \"enable\" | \"off\" | \"disable\"]"
                            .formatted(status));
        }
        return result;
    }
}
