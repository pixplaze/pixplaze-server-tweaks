package com.pixplaze.plugin.tweak;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public final class HelloMessage extends AbstractTweak {
    @EventHandler
    public void sayHelloOnJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        player.sendMessage("Hello %s!\nWelcome on our server!".formatted(player.getName()));
    }
}
