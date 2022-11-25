package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import java.util.*;
import java.util.stream.Collectors;

public final class TweakManager {


    private final PixplazeServerTweaks plugin;
    private final Set<ServerTweak> availableTweaks;
    private final Map<String, ServerTweak> loadedTweaks;

    private TweakManager(PixplazeServerTweaks plugin) {
        this(plugin, new HashSet<>(Set.of(
                new PermeableItemFrames(),
                new HelloMessage()
        )));
    }

    private TweakManager(PixplazeServerTweaks plugin, Set<ServerTweak> tweaks) {
        this.plugin = plugin;
        this.availableTweaks = tweaks;
        this.loadedTweaks = new HashMap<>();
    }

    public void loadTweaks(FileConfiguration config) {
        availableTweaks.forEach(tweak -> {
            var tweakName = tweak.getTweakName();
            var isTweakEnabled = config.getBoolean(tweakName);
            if (isTweakEnabled) enableTweak(tweak);
            loadedTweaks.put(tweak.getTweakName(), tweak);
        });
    }

    public void enableTweak(ServerTweak tweak) {
        tweak.setEnabled(true);
        plugin.registerListener(tweak);
    }

    public void enableTweak(String tweakName) {
        enableTweak(getTweak(tweakName));
    }

    public void disableTweak(ServerTweak tweak) {
        tweak.setEnabled(false);
        HandlerList.unregisterAll(tweak);
    }

    public void disableTweak(String tweakName) {
        disableTweak(getTweak(tweakName));
    }

    public Set<ServerTweak> getLoadedTweaks() {
        return availableTweaks;
    }

    public Set<ServerTweak> getEnabledTweaks() {
        return availableTweaks.stream()
                .filter(ServerTweak::isEnabled)
                .collect(Collectors.toSet());
    }

    public ServerTweak getTweak(String tweakName) {
        return loadedTweaks.get(tweakName);
    }
}
