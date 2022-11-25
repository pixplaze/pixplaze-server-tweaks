package com.pixplaze.plugin.tweak;

import com.pixplaze.plugin.PixplazeServerTweaks;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public final class PermeableItemFrames extends AbstractTweak {

    @EventHandler
    private void handler(PlayerInteractEntityEvent e) {
        var player = e.getPlayer();
        var entity = e.getRightClicked();

        if (entity instanceof ItemFrame frame) {
            var origin = Optional.ofNullable(frame.getOrigin());

            if (origin.isEmpty()) return;
            var block = origin.get()
                    .getBlock()
                    .getRelative(frame.getAttachedFace())
                    .getState();

            if (block instanceof ShulkerBox shulker && !shulker.isOpen()) {
                player.sendMessage("Shulker box is opened!");
                player.openInventory(shulker.getInventory());
                return;
            }

            if (shouldPlayerIgnoreFrame(player, frame, block)) {
                e.setCancelled(true);
                doInteract(block, player);
            }
        }
    }

    private void doInteract(BlockState block, Player player) {
        if (block instanceof InventoryHolder holder) {
            player.openInventory(holder.getInventory());
            return;
        }

        if (block instanceof EnderChest) {
            player.openInventory(player.getEnderChest());
            return;
        }

        switch (block.getType()) {
            case CRAFTING_TABLE -> player.openWorkbench(block.getLocation(), false);
            case LOOM -> player.openLoom(block.getLocation(), false);
            case ANVIL -> player.openAnvil(block.getLocation(), false);
            case STONECUTTER -> player.openStonecutter(block.getLocation(), false);
            case ENCHANTING_TABLE -> player.openEnchanting(block.getLocation(), false);
            case SMITHING_TABLE -> player.openSmithingTable(block.getLocation(), false);
            case CARTOGRAPHY_TABLE ->  player.openCartographyTable(block.getLocation(), false);
            case GRINDSTONE -> player.openGrindstone(block.getLocation(), false);
        }
    }

    private boolean shouldPlayerIgnoreFrame(Player player, ItemFrame frame, BlockState block) {
        var isBlockInteractive = false;
        var isPlayerSneaking = player.isSneaking();
        var isFrameEmpty = frame.getItem().getType().isEmpty();
        var isPlayerHandEmpty = player.getInventory().getItemInMainHand().getType().isEmpty();
        
        if (block instanceof InventoryHolder || block instanceof EnderChest) {
            isBlockInteractive = true;
        }
        
        switch (block.getType()) {
            case    CRAFTING_TABLE,
                    LOOM,
                    ANVIL,
                    STONECUTTER,
                    ENCHANTING_TABLE,
                    SMITHING_TABLE,
                    CARTOGRAPHY_TABLE,
                    GRINDSTONE ->
                isBlockInteractive = true;
        }

        return isBlockInteractive && 
                (isFrameEmpty || !isPlayerSneaking) &&
                (isPlayerHandEmpty || !isPlayerSneaking);
    }

    private void verboseTweakObject() {
        var component = Component.text();
        PixplazeServerTweaks.getInstance()
                .ifPresent(plugin -> HandlerList.getRegisteredListeners(plugin).forEach(listener -> {
                    component.append(Component.text("%s: @%s".formatted(
                            ((ServerTweak) listener.getListener()).getClass().getSimpleName(), System.identityHashCode(listener.getListener()))
                    ));

                    plugin.getServer().sendMessage(component);
                }));
    }
}
