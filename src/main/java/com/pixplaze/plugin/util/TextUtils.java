package com.pixplaze.plugin.util;

import com.pixplaze.plugin.tweaks.ServerTweak;
import org.bukkit.ChatColor;

public class TextUtils {

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

    public static String getShowCommandMessage(ServerTweak tweak) {
        return String.format("%s%nDescription:%n%s%n",
                getTweakStatusMessage(tweak),
                tweak.getDescription()
        );
    }

    public static String getTweakStatusLabel(ServerTweak tweak, boolean uppercase) {
        var color = tweak.isEnabled() ?
                ChatColor.GREEN :
                ChatColor.RED;
        var text = TextUtils.getStatusLabel(tweak.isEnabled());
        if (uppercase) text = text.toUpperCase();
        return color + text + ChatColor.RESET;
    }

    public static String getTweakStatusMessage(ServerTweak tweak) {
        return getTweakStatusMessage(tweak, true);
    }

    public static String getTweakStatusMessage(ServerTweak tweak, boolean padding) {
        return String.format("Tweak: %s, status: %s",
                tweak.getTweakName(),
                getTweakStatusLabel(tweak, true));
    }

    public static String getMessageIfDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" has been %s."
                .formatted(tweakName, TextUtils.getStatusLabel(status));
        return ChatColor.YELLOW + message + ChatColor.RESET;
    }

    public static String getMessageIfAlreadyDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" is already %s."
                .formatted(tweakName, TextUtils.getStatusLabel(status));
        return ChatColor.RED + message + ChatColor.RESET;
    }
}
