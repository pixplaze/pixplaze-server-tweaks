package com.pixplaze.plugin.commands;

import com.pixplaze.plugin.tweaks.ServerTweak;
import com.pixplaze.plugin.tweaks.TweakManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TweakTabCompleter implements TabCompleter {
    private final TweakManager tweakManager;

    public TweakTabCompleter(TweakManager tweakManager) {
        this.tweakManager = tweakManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        List<String> complete = List.of();

        switch (args.length) {
            case 1 -> {
                complete = TweakCommandAction.available();
            }
            case 2 -> {
                var action = TweakCommandAction.from(args[0]);
                if (action == TweakCommandAction.ENABLE) {
                    complete = tweakManager.getDisabledTweaks().stream()
                            .map(ServerTweak::getTweakName)
                            .collect(Collectors.toList());
                    break;
                }

                if (action == TweakCommandAction.DISABLE) {
                    complete = tweakManager.getEnabledTweaks().stream()
                            .map(ServerTweak::getTweakName)
                            .collect(Collectors.toList());
                    break;
                }

                if (action == TweakCommandAction.STATUS ||
                    action == TweakCommandAction.SHOW) {
                    complete = tweakManager.getLoadedTweaks().stream()
                            .map(ServerTweak::getTweakName)
                            .collect(Collectors.toList());
                }
            }
        }

        return complete;
    }
}
