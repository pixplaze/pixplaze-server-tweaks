package com.pixplaze.plugin;

import com.pixplaze.plugin.commands.TweakCommandExecutor;
import com.pixplaze.plugin.commands.TweakTabCompleter;
import com.pixplaze.plugin.tweaks.*;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public final class PixplazeServerTweaks extends JavaPlugin {

    private final Logger logger;
    private final FileConfiguration config;
    private final TweakManager tweakManager;
    private final TweakCommandExecutor tweakCommandExecutor;
    private static PixplazeServerTweaks instance;


    public PixplazeServerTweaks() {
        logger = this.getLogger();
        config = getConfig()
                .options()
                .copyDefaults(true)
                .configuration();
        instance = this;
        tweakManager = new TweakManager(this);
        tweakCommandExecutor = new TweakCommandExecutor(tweakManager);
    }

    public static Optional<PixplazeServerTweaks> getInstance() {
        return Optional.of(instance);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        tweakManager.loadTweaks(config);

        var tweakCommand = Objects.requireNonNull(getCommand("tweak"));
        var whatCommand = Objects.requireNonNull(getCommand("what"));

        tweakCommand.setExecutor(tweakCommandExecutor);
        tweakCommand.setTabCompleter(new TweakTabCompleter(tweakManager));
    }

    @Override
    public void onDisable() {}
}
