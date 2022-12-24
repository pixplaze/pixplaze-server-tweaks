package com.pixplaze.plugin.util;

import com.pixplaze.plugin.tweaks.ServerTweak;
import org.bukkit.Color;

public class TextComponentUtils {

    public static String getShowCommandMessage(ServerTweak tweak) {
        return new StringBuilder()
                .append(getStatusCommandMessage(tweak))
                .append("\n")
                .append("Description:\n")
                .append(tweak.getDescription())
                .toString();
    }

    public static String getColoredTweakStatus(boolean status) {
        return getColoredTweakStatus(status, true);
    }

    public static String getColoredTweakStatus(boolean status, boolean uppercase) {
//        var color = status ?
//                TextColor.color(Color.LIME.asRGB()) :
//                TextColor.color(Color.ORANGE.asRGB());
        var text = TweakUtils.isTweakEnabledLabel(status);
        if (uppercase) text = text.toUpperCase();
        return text;
    }

    public static String getStatusCommandMessage(ServerTweak tweak) {
        var str = TweakUtils.isTweakEnabledLabel(tweak.isEnabled());
        var status = getColoredTweakStatus(tweak.isEnabled());
        return Component.text("Tweak: ")
                .append(Component.text(tweak.getTweakName()))
                .append(Component.text("\n"))
                .append(Component.text("Status: "))
                .append(status);
    }

    public static String getMessageIfDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" has been %s."
                .formatted(tweakName, TweakUtils.isTweakEnabledLabel(status));
        return Component.text(message)
                .color(TextColor.color(Color.YELLOW.asRGB()));
    }

    public static String getMessageIfAlreadyDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" is already %s."
                .formatted(tweakName, TweakUtils.isTweakEnabledLabel(status));
        return Component.text(message)
                .color(TextColor.color(Color.ORANGE.asRGB()));
    }
}