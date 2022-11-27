package com.pixplaze.plugin.tweaks;

import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Optional;

public final class PermeableItemFramesTweak implements ServerTweak {

    private boolean isEnabled = false;

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
                e.setCancelled(true);
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
