package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TweakManager {

    private final PixplazeServerTweaks plugin;
    private static final Set<ServerTweak> availableTweaks;
    private static final Map<String, ServerTweak> loadedTweaks;

    static {
        availableTweaks = new HashSet<>(Set.of(
                new PermeableItemFrames(),
                new HelloMessage()
        ));
        loadedTweaks = new HashMap<>();
    }

    public TweakManager() {
        plugin = PixplazeServerTweaks.getInstance().orElseThrow();
        availableTweaks.forEach(tweak -> loadedTweaks.put(tweak.getTweakName(), tweak));
    }

    public void enableTweak(ServerTweak tweak) {
        plugin.registerListener(tweak);
    }

    public void disableTweak(ServerTweak tweak) {
        HandlerList.unregisterAll(tweak);
    }

    public Set<ServerTweak> getLoadedTweaks() {
        return availableTweaks;
    }

    public Set<ServerTweak> getEnabledTweaks() {
        return availableTweaks.stream()
                .filter(ServerTweak::isEnabled)
                .collect(Collectors.toSet());
    }
}
