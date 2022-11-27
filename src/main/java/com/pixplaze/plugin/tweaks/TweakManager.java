package com.pixplaze.plugin.tweaks;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.exceptions.TweakConfigurationException;
import com.pixplaze.plugin.exceptions.TweakNotFoundException;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getPluginManager;

public final class TweakManager {

    private final PixplazeServerTweaks plugin;
    private final Set<ServerTweak> loadedTweaks;

    public TweakManager(PixplazeServerTweaks plugin) {
        this(plugin, new HashSet<>(Set.of(
                new PermeableItemFramesTweak(),
                new HelloMessageTweak()
        )));
    }

    private TweakManager(PixplazeServerTweaks plugin, Set<ServerTweak> tweaks) {
        this.plugin = plugin;
        this.loadedTweaks = tweaks;
    }

    public void loadTweaks(FileConfiguration fileConfiguration) {
        var config = Optional.ofNullable(fileConfiguration.getConfigurationSection("tweaks"))
                .orElseThrow(TweakConfigurationException::new);

        loadedTweaks.forEach(tweak -> {
            var tweakName = tweak.getTweakName();
            var isTweakEnabled = config.getBoolean(tweakName);
            if (isTweakEnabled) enableTweak(tweak);
        });

        verboseTweaks();
    }

    public void enableTweak(ServerTweak tweak) {
        if (tweak.isEnabled()) return;
        tweak.setEnabled(true);
        getPluginManager().registerEvents(tweak, plugin);
        saveConfig(tweak.getTweakName(), true);
    }

    public ServerTweak enableTweak(String tweakName) {
        var tweak = Optional.ofNullable(getTweak(tweakName))
                .orElseThrow(TweakNotFoundException::new);

        enableTweak(tweak);
        return tweak;
    }

    public void disableTweak(ServerTweak tweak) {
        if (!tweak.isEnabled()) return;
        tweak.setEnabled(false);
        HandlerList.unregisterAll(tweak);
        saveConfig(tweak.getTweakName(), false);
    }

    public ServerTweak disableTweak(String tweakName) {
        var tweak = Optional.ofNullable(getTweak(tweakName))
                .orElseThrow(TweakNotFoundException::new);
        disableTweak(tweak);
        return tweak;
    }

    public Set<ServerTweak> getLoadedTweaks() {
        return loadedTweaks;
    }

    public Set<ServerTweak> getEnabledTweaks() {
        return loadedTweaks.stream()
                .filter(ServerTweak::isEnabled)
                .collect(Collectors.toSet());
    }

    public Set<ServerTweak> getDisabledTweaks() {
        return loadedTweaks.stream()
                .filter(tweak -> !tweak.isEnabled())
                .collect(Collectors.toSet());
    }

    public ServerTweak getTweak(String tweakName) {
        return loadedTweaks.stream()
                .filter(tweak -> tweak.getTweakName().equalsIgnoreCase(tweakName))
                .findFirst()
                .orElseThrow(() -> new TweakNotFoundException(tweakName));
    }

    private void saveConfig(String tweakName, boolean status) {
        plugin.getConfig().set("tweaks.%s".formatted(tweakName), status);
        plugin.saveConfig();
    }

    public void verboseTweaks() {
        var listeners = HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener)
                .toList();
        verboseTweakObjects(loadedTweaks, "Available tweaks");
        verboseTweakObjects(listeners, "Listeners");
    }

    private void verboseTweakObjects(Collection<?> tweaks, String title) {
        var component = Component.text(title + ": ");

        tweaks.forEach(listener -> {
            var name = listener.getClass().getSimpleName();
            var hashCode = Integer.toHexString(System.identityHashCode(listener));
            var message= component.append(Component.text("%s: @%s".formatted(name, hashCode)));

            plugin.getServer().sendMessage(message);
        });
    }
}
