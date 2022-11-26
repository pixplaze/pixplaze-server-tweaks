package com.pixplaze.plugin.tweak;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public final class HelloMessage implements ServerTweak {

    private boolean isEnabled = false;

    @EventHandler
    public void sayHelloOnJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        player.sendMessage("Hello %s!\nWelcome on our server!".formatted(player.getName()));
    }

    @Override
    public void setEnabled(boolean status) {
        this.isEnabled = status;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
