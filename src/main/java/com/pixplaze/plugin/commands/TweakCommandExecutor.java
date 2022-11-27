package com.pixplaze.plugin.commands;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.tweaks.TweakManager;
import com.pixplaze.plugin.util.TextComponentUtils;

import net.kyori.adventure.text.Component;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
            switch (args.length) {
                case 0 -> {
                    return tryRunListCommand(sender);
                }
                case 1 -> {
                    var action = TweakCommandAction.from(args[0]);

                    if (action == TweakCommandAction.LIST) {
                        return tryRunListCommand(sender);
                    }

                    if (action == TweakCommandAction.UNDEFINED) {
                        return tryRunShowCommand(args[0], sender);
                    }
                    return false;
                }
                case 2 -> {
                    var arg0 = args[0];
                    var arg1 = args[1];
                    var action = TweakCommandAction.from(arg0);

                    if (action == TweakCommandAction.STATUS) {
                        return tryRunStatusCommand(arg1, sender);
                    }

                    if (action == TweakCommandAction.SHOW) {
                        return tryRunShowCommand(arg1, sender);
                    }

                    if (action == TweakCommandAction.ENABLE) {
                        return tryRunEnableCommand(arg1, true, sender);
                    }

                    if (action == TweakCommandAction.DISABLE) {
                        return tryRunEnableCommand(arg1, false, sender);
                    }

                    return false;
                }
                default -> {
                    return false;
                }
            }
        } catch (RuntimeException e) {
            logger.warning(e.getMessage());
            return false;
        }
    }

    private boolean tryRunShowCommand(String tweakName, CommandSender sender) {
        AtomicBoolean success = new AtomicBoolean(false);
        Optional.ofNullable(tweakManager.getTweak(tweakName))
                .ifPresent(tweak -> {
                    success.set(true);
                    sender.sendMessage(TextComponentUtils.getShowCommandMessage(tweak));
                });
        return success.get();
    }

    private boolean tryRunStatusCommand(String tweakName, @NotNull CommandSender sender) {
        AtomicBoolean success = new AtomicBoolean(false);
        Optional.ofNullable(tweakManager.getTweak(tweakName))
                .ifPresent(tweak -> {
                    success.set(true);
                    sender.sendMessage(TextComponentUtils.getStatusCommandMessage(tweak));
                });
        return success.get();
    }

    private boolean tryRunListCommand(CommandSender sender) {
        var title = Component.text("List Of Available Server Tweaks:")
                .append(Component.text("\n"));

        var list = title.append(tweakManager.getLoadedTweaks().stream()
                .map(tweak -> Component.text()
                        .append(Component.text(tweak.getTweakName()))
                        .append(Component.text(": "))
                        .append(TextComponentUtils.getColoredTweakStatus(tweak.isEnabled())))
                .reduce((curr, next) -> curr.append(Component.text("\n")).append(next)).orElseThrow());

        sender.sendMessage(list);
        return true;
    }

    private boolean tryRunEnableCommand(final String tweakName, final boolean enable, CommandSender sender) {
        var tweak = tweakManager.getTweak(tweakName);

        if (tweak.isEnabled() == enable) {
            var message = TextComponentUtils.getMessageIfAlreadyDefined(tweakName, enable);
            sender.sendMessage(message);
            return true;
        }

        if (enable) tweakManager.enableTweak(tweak);
        else tweakManager.disableTweak(tweak);

        plugin.getServer().sendMessage(TextComponentUtils.getMessageIfDefined(tweakName, enable));

        return true;
    }
}
