package com.pixplaze.plugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Optional;

public class WhatCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            var playerProfile = player.getPlayerProfile();
            var playerSkinUri = Optional.ofNullable(playerProfile.getTextures().getSkin());
            var isTexturesEmpty = playerProfile.getTextures().isEmpty();
            var color = isTexturesEmpty ?
                    TextColor.color(Color.RED.asRGB()) :
                    TextColor.color(Color.GREEN.asRGB());

            sender.sendMessage(Component.text("Is textures empty: ")
                    .append(Component.text("%b\n".formatted(isTexturesEmpty)).color(color))
                    .append(Component.text("Set skin URL: %s".formatted(playerSkinUri.map(URL::toString).orElse("empty")))
                            .color(TextColor.color(Color.AQUA.asRGB())))
                    .append(Component.text()));

            return true;
        } else return false;
    }
}
