package com.pixplaze.plugin.util;

public class TweakUtils {

    /**
     * Преобразует логическое значение boolean в строковое представление
     * {@code true = ["enabled" | "on"], false = ["disabled" | "off"].}
     * @param status статус твика (true - включен, false - выключен);
     * @param shorted должен ли быть укорочен статус твика (true - на on,
     *                false - на off);
     * @return строковое предстваление статуса твика.
     */
    public static String statusFromBoolean(final boolean status, final boolean shorted) {

        return shorted ?
                status ? "on" : "off" :
                status ? "enabled" : "disabled";
    }

    /**
     * Преобразует логическое значение boolean в строковое представление
     * {@code true = "enabled", false = "disabled".}
     * @param status статус твика (true - включен, false - выключен);
     * @return строковое предстваление статуса твика.
     * @see TweakUtils#statusFromBoolean(boolean, boolean)
     */
    public static String statusFromBoolean(final boolean status) {
        return statusFromBoolean(status, false);
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
