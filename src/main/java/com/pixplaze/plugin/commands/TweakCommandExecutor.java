package com.pixplaze.plugin.commands;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.tweaks.TweakManager;
import com.pixplaze.plugin.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                    var actionInput = args[0];
                    var targetInput = args[1];
                    var action = TweakCommandAction.from(actionInput);

                    if (action == TweakCommandAction.STATUS) {
                        return tryRunStatusCommand(targetInput, sender);
                    }

                    if (action == TweakCommandAction.SHOW) {
                        return tryRunShowCommand(targetInput, sender);
                    }

                    if (action == TweakCommandAction.ENABLE) {
                        return tryRunEnableCommand(targetInput, true, sender);
                    }

                    if (action == TweakCommandAction.DISABLE) {
                        return tryRunEnableCommand(targetInput, false, sender);
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
                    sender.sendMessage(TextUtils.getShowCommandMessage(tweak));
                });
        return success.get();
    }

    private boolean tryRunStatusCommand(String tweakName, @NotNull CommandSender sender) {
        AtomicBoolean success = new AtomicBoolean(false);
        Optional.ofNullable(tweakManager.getTweak(tweakName))
                .ifPresent(tweak -> {
                    success.set(true);
                    sender.sendMessage(TextUtils.getTweakStatusMessage(tweak));
                });
        return success.get();
    }

    private boolean tryRunListCommand(CommandSender sender) {
        var tweakNameList = tweakManager.getLoadedTweaks().stream()
                .map(TextUtils::getTweakStatusMessage)
                .collect(Collectors.joining("\n"));

        var message = String.format(
                "List Of Available Server Tweaks:\n%s",
                tweakNameList);
        sender.sendMessage(message);

        return true;
    }

    private boolean tryRunEnableCommand(final String tweakName, final boolean enable, CommandSender sender) {
        var tweak = tweakManager.getTweak(tweakName);
        sender.sendMessage("Command: %s, tweak.isEnabled() %s".formatted(enable, tweak.isEnabled()));
        if (tweak.isEnabled() == enable) {
            var message = TextUtils.getMessageIfAlreadyDefined(tweakName, enable);
            sender.sendMessage(message);
            return true;
        }

        if (enable) tweakManager.enableTweak(tweak);
        else tweakManager.disableTweak(tweak);

        Bukkit.broadcastMessage(TextUtils.getMessageIfDefined(tweakName, enable));

        return true;
    }
}
