package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;

public abstract class AbstractTweak implements ServerTweak {
    private boolean isEnabled = false;
    private String message = "%s \"%s\" tweak in AbstractTweak.class";

    @Override
    public final void enable() {
        isEnabled = true;
        ServerTweak.super.enable();
        PixplazeServerTweaks.getInstance().ifPresent(plugin -> {
            plugin.getLogger().warning(message.formatted("Enabling", getTweakName()));
            plugin.getLogger().warning("isEnabled: %b".formatted(this.isEnabled));
        });
    }

    @Override
    public final void disable() {
        isEnabled = false;
        ServerTweak.super.disable();
        PixplazeServerTweaks.getInstance().ifPresent(plugin -> {
            plugin.getLogger().warning(message.formatted("Disabling", getTweakName()));
            plugin.getLogger().warning("isEnabled: %b".formatted(this.isEnabled));
        });
    }

    @Override
    public final boolean isEnabled() {
        return isEnabled;
    }
}
