package com.pixplaze.plugin.tweaks;

import org.apache.commons.lang.StringUtils;
import org.bukkit.event.Listener;

public interface ServerTweak extends Listener {

    void setEnabled(final boolean status);

    boolean isEnabled();

    String getDescription();

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
