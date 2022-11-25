package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.exception.TweakConfigurationException;
import com.pixplaze.plugin.exception.TweakNotFoundException;
import com.pixplaze.plugin.util.TweakUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class TweakCommands implements CommandExecutor {

    private final Set<ServerTweak> tweaks;
    private final List<RegisteredListener> handlers;
    private final PixplazeServerTweaks plugin;
    private final Logger logger;
    private final FileConfiguration config;

    public TweakCommands() {
        this.plugin = PixplazeServerTweaks.getInstance().orElseThrow();
        this.tweaks = plugin.getServerTweaks();
        this.handlers = HandlerList.getRegisteredListeners(plugin);
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            var tweakName = args[0];
            var action = args[1];

//            logger.warning("Command arguments:");
//            for (var i = 0; i < args.length; i++) {
//                logger.warning("[%d]: %s".formatted(i, args[i]));
//            }

            var tweak = tweaks.stream()
                    .filter(twk -> twk.getTweakName().equalsIgnoreCase(tweakName))
                    .findFirst()
                    .orElseThrow(TweakNotFoundException::new);

            switch (action.toLowerCase()) {
                case "on", "enable" -> setTweakStatus(tweak, sender, true);
                case "off", "disable" -> setTweakStatus(tweak, sender, false);
                default -> throw new CommandException("No such command %s".formatted(action));
            }

        } catch (CommandException | TweakNotFoundException | ArrayIndexOutOfBoundsException e) {
            logger.warning(e.getMessage());
            return false;
        }

        return true;
    }

    private void setTweakStatus(final ServerTweak tweak, final CommandSender sender, boolean status) {
        var message = "Server tweak %s has been %s."
                .formatted(tweak.getTweakName(), TweakUtils.statusFromBoolean(status));
        var component = Component.text(message)
                .color(TextColor.color(Color.YELLOW.asRGB()));

        if (tweak.isEnabled() == status) {
            message = "Server tweak \"%s\" is already %s."
                    .formatted(tweak.getTweakName(), TweakUtils.statusFromBoolean(status));
            component = Component.text(message)
                    .color(TextColor.color(Color.YELLOW.asRGB()));

            sender.sendMessage(component);
            return;
        }

        if (status) tweak.enable();
        else tweak.disable();

        logger.warning("\"%s\" is now %b".formatted(tweak.getTweakName(), tweak.isEnabled()));

        config.set("tweaks.%s".formatted(tweak.getTweakName()), tweak.isEnabled());
        plugin.saveConfig();
//        try {
//            config.options()
//                    .copyDefaults(true)
//                    .configuration()
//                    .save(plugin.getDataFolder() + "/config.yml");
//        } catch (IOException e) {
//            logger.warning(e.getMessage());
//        }
        plugin.getServer().sendMessage(component);
    }
}
