package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.exception.TweakConfigurationException;
import com.pixplaze.plugin.exception.TweakNotFoundException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getPluginManager;

public final class TweakManager {

    private final PixplazeServerTweaks plugin;
    private final Set<ServerTweak> availableTweaks;
    private final Map<String, ServerTweak> loadedTweaks;

    public TweakManager(PixplazeServerTweaks plugin) {
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

    public void loadTweaks(FileConfiguration fileConfiguration) {
        var config = Optional.ofNullable(fileConfiguration.getConfigurationSection("tweaks"))
                .orElseThrow(TweakConfigurationException::new);

        availableTweaks.forEach(tweak -> {
            var tweakName = tweak.getTweakName();
            var isTweakEnabled = config.getBoolean(tweakName);
            if (isTweakEnabled) enableTweak(tweak);
            loadedTweaks.put(tweakName, tweak);
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

    private void onTweakNotFound() {
        throw new TweakNotFoundException();
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

    private void saveConfig(String tweakName, boolean status) {
        plugin.getConfig().set("tweaks.%s".formatted(tweakName), status);
        plugin.saveConfig();
    }

    public void verboseTweaks() {
        var listeners = HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener)
                .toList();
        verboseTweakObjects(availableTweaks, "Available tweaks");
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
