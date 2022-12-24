package com.pixplaze.plugin;

import com.pixplaze.plugin.commands.TweakCommandExecutor;
import com.pixplaze.plugin.commands.TweakTabCompleter;
import com.pixplaze.plugin.tweaks.*;
import org.bukkit.configuration.file.FileConfiguration;
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
        var command = Objects.requireNonNull(getCommand("tweak"));
        command.setExecutor(tweakCommandExecutor);
        command.setTabCompleter(new TweakTabCompleter(tweakManager));
    }

    @Override
    public void onDisable() {}
}
