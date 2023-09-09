package com.pixplaze.plugin.tweaks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public final class PermeableItemFramesTweak implements ServerTweak {

    private boolean isEnabled = false;

    @EventHandler
    private void handler(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var entity = event.getRightClicked();

        if (event.getHand().equals(EquipmentSlot.HAND) && entity instanceof ItemFrame frame) {
            var origin = Optional.of(frame.getAttachedFace());
            var block = frame.getLocation().getBlock().getRelative(origin.get());

            if (shouldPlayerIgnoreFrame(player, frame, block.getType())) {
                event.setCancelled(true);
                doInteract(block.getState(), player);
            }
        }
    }

    private void doInteract(BlockState block, Player player) {
        if (block instanceof Container holder) {
            player.openInventory(holder.getInventory());
            return;
        }

        if (block instanceof EnderChest) {
            player.openInventory(player.getEnderChest());
            return;
        }

        switch (block.getType()) {
            case CRAFTING_TABLE -> player.openWorkbench(block.getLocation(), false);
            case LOOM -> player.openInventory(Bukkit.createInventory(null, InventoryType.LOOM));
            case ANVIL -> player.openInventory(Bukkit.createInventory(null, InventoryType.ANVIL));
            case STONECUTTER -> player.openInventory(Bukkit.createInventory(null, InventoryType.STONECUTTER));
            case ENCHANTING_TABLE -> player.openInventory(Bukkit.createInventory(null, InventoryType.ENCHANTING));
            case SMITHING_TABLE -> player.openInventory(Bukkit.createInventory(null, InventoryType.SMITHING));
            case CARTOGRAPHY_TABLE -> player.openInventory(Bukkit.createInventory(null, InventoryType.CARTOGRAPHY));
            case GRINDSTONE -> player.openInventory(Bukkit.createInventory(null, InventoryType.GRINDSTONE));
            case LECTERN -> player.openInventory(Bukkit.createInventory(null, InventoryType.LECTERN));
        }
    }

    private boolean shouldPlayerIgnoreFrame(Player player, ItemFrame frame, Material block) {
        var isBlockInteractive = block.isInteractable();
        var isPlayerSneaking = player.isSneaking();
        var isFrameEmpty = frame.getItem().getType().isAir();
        var isPlayerHandEmpty = player.getInventory().getItemInMainHand().getType().isAir();

        return isBlockInteractive && 
                (isFrameEmpty || !isPlayerSneaking) &&
                (isPlayerHandEmpty || !isPlayerSneaking);
    }

    @Override
    public void setEnabled(boolean status) {
        this.isEnabled = status;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getDescription() {
        return """
                This tweak allows you to click on a frame to use the block it's
                attached to. For example, by clicking on a frame
                attached to a chest, the chest will open.""";
    }
}
