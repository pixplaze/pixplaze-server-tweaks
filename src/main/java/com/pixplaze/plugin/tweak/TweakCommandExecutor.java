package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.util.TweakUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class TweakCommandExecutor implements CommandExecutor {
    private final PixplazeServerTweaks plugin;
    private final TweakManager tweakManager;
    private final Logger logger;

    public TweakCommandExecutor(TweakManager tweakManager) {
        this.plugin = PixplazeServerTweaks.getInstance().orElseThrow();
        this.tweakManager = tweakManager;
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args)
    {
        try {
            var tweakName = args[0];
            var action = args[1];

            handleChangeStatusCommand(tweakName, action, sender);
            tweakManager.verboseTweaks();
        } catch (RuntimeException e) {
            logger.warning(e.getMessage());
            return false;
        }

        return true;
    }

    private void handleChangeStatusCommand(final String tweakName, final String status, final CommandSender sender) {
        var tweak = tweakManager.getTweak(tweakName);
        var booleanStatus = TweakUtils.booleanFromStatus(status);
        var stringStatus = TweakUtils.statusFromBoolean(booleanStatus);

        var message = "Server tweak \"%s\" has been %s."
                .formatted(tweakName, stringStatus);
        var component = Component.text(message)
                .color(TextColor.color(Color.YELLOW.asRGB()));

        if (tweak.isEnabled() == booleanStatus) {
            message = "Server tweak \"%s\" is already %s."
                    .formatted(tweakName, stringStatus);

            sender.sendMessage(component.content(message));
            return;
        }

        if (booleanStatus)  tweakManager.enableTweak(tweak);
        else                tweakManager.disableTweak(tweak);


        plugin.getServer().sendMessage(component);
    }
}
