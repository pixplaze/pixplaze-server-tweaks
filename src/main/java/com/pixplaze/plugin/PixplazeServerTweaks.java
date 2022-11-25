package com.pixplaze.plugin;

import com.pixplaze.plugin.exception.TweakConfigurationException;
import com.pixplaze.plugin.tweak.HelloMessage;
import com.pixplaze.plugin.tweak.PermeableItemFrames;
import com.pixplaze.plugin.tweak.ServerTweak;
import com.pixplaze.plugin.tweak.TweakCommands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class PixplazeServerTweaks extends JavaPlugin {

    private final Logger logger;
    private final FileConfiguration config;

    private final Set<ServerTweak> tweaks;
    private static PixplazeServerTweaks instance;


    public PixplazeServerTweaks() {
        logger = this.getLogger();
        config = getConfig()
                .options()
                .copyDefaults(true)
                .configuration();
        instance = this;
        tweaks = new HashSet<>(Set.of(
                new PermeableItemFrames(),
                new HelloMessage()
        ));
    }

    public static Optional<PixplazeServerTweaks> getInstance() {
        return Optional.of(instance);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadServerTweaks();

        Objects.requireNonNull(getCommand("tweak"))
                .setExecutor(new TweakCommands());
    }

    private void loadServerTweaks() {
        logger.warning("Reading server tweak list...");
        tweaks.forEach(tweak -> {
            var tweakName = tweak.getTweakName();
            var enabledConf = config.getConfigurationSection("tweaks");
            var isTweakEnabled = Optional.ofNullable(enabledConf)
                    .orElseThrow(TweakConfigurationException::new)
                    .getBoolean(tweakName);

            if (isTweakEnabled) {
                tweak.enable();
                registerListener(tweak);
            }

            logger.warning("\"%s\" tweak is loaded %s.".formatted(tweakName, isTweakEnabled ? "and enabled" : "but disabled"));
        });
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {}

    public Set<ServerTweak> getServerTweaks() {
        return this.tweaks;
    }

}
