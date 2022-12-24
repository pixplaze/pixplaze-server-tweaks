package com.pixplaze.plugin.util;

import com.pixplaze.plugin.tweaks.ServerTweak;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class TextComponentUtils {

    public static TextComponent getShowCommandMessage(ServerTweak tweak) {
        return Component.text()
                .append(getStatusCommandMessage(tweak))
                .append(Component.text("\n"))
                .append(Component.text("Description:\n"))
                .append(Component.text(tweak.getDescription()))
                .build();
    }

    public static TextComponent getColoredTweakStatus(boolean status) {
        return getColoredTweakStatus(status, true);
    }

    public static TextComponent getColoredTweakStatus(boolean status, boolean uppercase) {
        var color = status ?
                TextColor.color(Color.LIME.asRGB()) :
                TextColor.color(Color.ORANGE.asRGB());
        var text = TweakUtils.isTweakEnabledLabel(status);
        if (uppercase) text = text.toUpperCase();
        return Component.text(text).color(color);
    }

    public static TextComponent getStatusCommandMessage(ServerTweak tweak) {
        var str = TweakUtils.isTweakEnabledLabel(tweak.isEnabled());
        var status = getColoredTweakStatus(tweak.isEnabled());
        return Component.text("Tweak: ")
                .append(Component.text(tweak.getTweakName()))
                .append(Component.text("\n"))
                .append(Component.text("Status: "))
                .append(status);
    }

    public static TextComponent getMessageIfDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" has been %s."
                .formatted(tweakName, TweakUtils.isTweakEnabledLabel(status));
        return Component.text(message)
                .color(TextColor.color(Color.YELLOW.asRGB()));
    }

    public static TextComponent getMessageIfAlreadyDefined(String tweakName, boolean status) {
        var message = "Server tweak \"%s\" is already %s."
                .formatted(tweakName, TweakUtils.isTweakEnabledLabel(status));
        return Component.text(message)
                .color(TextColor.color(Color.ORANGE.asRGB()));
    }
}