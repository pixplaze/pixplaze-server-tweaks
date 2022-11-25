package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface ServerTweak extends Listener {
    @Deprecated(forRemoval = true)
    default void enable() {
        PixplazeServerTweaks.getInstance()
                .ifPresent(plugin -> plugin.registerListener(this));

    }

    @Deprecated(forRemoval = true)
    default void disable() {
        HandlerList.unregisterAll(this);
    }

    void setEnabled(final boolean status);

    boolean isEnabled();

    default String getTweakName() {
        var tweakClassName = getClass().getSimpleName();
        var chars = StringUtils.removeEnd(tweakClassName, "Tweak").toCharArray();
        var name = new StringBuilder();

        chars[0] = Character.toLowerCase(chars[0]);

        for (var c: chars) {
            if (Character.isUpperCase(c))
                name.append('-').append(Character.toLowerCase(c));
            else name.append(c);
        }

        return name.toString();
    }
}
