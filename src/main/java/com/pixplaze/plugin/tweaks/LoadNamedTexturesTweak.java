package com.pixplaze.plugin.tweaks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.reflected.SkinChangeProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.profile.PlayerTextures;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

public class LoadNamedTexturesTweak implements ServerTweak, Listener {

    private final PixplazeServerTweaks plugin;
    private final HttpClient httpClient;

    private final Gson gson;
    private final String usernameToUuidEndpoint = "https://api.mojang.com/users/profiles/minecraft/";
    private final String uuidToTexturesEndpoint = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private boolean isEnabled = false;

    public LoadNamedTexturesTweak(PixplazeServerTweaks plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.httpClient = HttpClient.newHttpClient();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException, InterruptedException {
        var player = e.getPlayer();
        setPlayerTextures(player);
        player.hidePlayer(plugin, player);
    }

    private void setPlayerTextures(Player player) throws IOException, InterruptedException {
        var username = player.getName();
        var profile = player.getPlayerProfile();
        var textures = profile.getTextures();
        var uuid = fetchUsernameToUuid(username);
        var base64Textures = fetchUuidToBase64Textures(uuid);
//        var jsonTextures = gson.fromJson(Base64Coder.decodeString(base64Textures), JsonObject.class)
//                .get("textures").getAsJsonObject();
//
//        var jsonSkin = jsonTextures.get("SKIN").getAsJsonObject();
//        var jsonCape = jsonTextures.get("CAPE").getAsJsonObject();
//
//        var skinUrl = new URL(jsonSkin.get("url").getAsString());
//        var skinModel = jsonSkin.get("metadata") == null ?
//                PlayerTextures.SkinModel.CLASSIC :
//                PlayerTextures.SkinModel.valueOf(
//                        jsonSkin.get("metadata").getAsJsonObject()
//                                .get("model").getAsString()
//                                .toUpperCase());
//        var capeUrl = new URL(jsonCape.get("url").getAsString());

        SkinChangeProvider.changeSkin(player, base64Textures);

//        textures.setSkin(skinUrl, skinModel);
//        textures.setCape(capeUrl);
//
//        profile.setTextures(textures);
//        profile.hasTextures();
//        player.setPlayerProfile(profile);


        plugin.getServer().sendMessage(Component.text(("""
                %nPlayer %s textures is empty: %s,
                is signed: %s,
                is profile complete: %s,
                profile has textures: %s,
                skin url: %s""").
                formatted(
                        player.getName(),
                        player.getPlayerProfile().getTextures().isEmpty(),
                        player.getPlayerProfile().getTextures().isSigned(),
                        player.getPlayerProfile().isComplete(),
                        player.getPlayerProfile().hasTextures(),
                        player.getPlayerProfile().getTextures().getSkin())));
        plugin.getServer().sendMessage(Component.text(base64Textures));
//        plugin.getServer().sendMessage(Component.text("Set textures: %s".
//                formatted(player.getPlayerProfile().getTextures().getSkin())));
    }

    private String fetchUsernameToUuid(String username) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(usernameToUuidEndpoint + username))
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var body = response.body();
        var json = gson.fromJson(body, JsonObject.class);
        return json.get("id").getAsString();
    }

    private String fetchUuidToBase64Textures(String uuid) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(uuidToTexturesEndpoint + uuid))
                .setHeader("User-Agent", "Mozilla/5.0")
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var body = response.body();
        var json = gson.fromJson(body, JsonObject.class);
        var jsonProperties = json.get("properties").getAsJsonArray();
        var atomicJsonTextures = new AtomicReference<String>();

        for (var jsonElement : jsonProperties) {
            var jsonProperty = jsonElement.getAsJsonObject();
            if (jsonProperty.get("name").getAsString().equals("textures")) {
                atomicJsonTextures.set(jsonProperty.get("value").getAsString());
                break;
            }
        }

        return atomicJsonTextures.get();
    }

    @Override
    public void setEnabled(boolean status) {
        this.isEnabled = status;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getDescription() {
        return "Tweak for loading named player textures (skins). Works only on offline mode servers.";
    }
}
