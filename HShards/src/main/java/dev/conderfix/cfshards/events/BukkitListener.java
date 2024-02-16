package dev.conderfix.cfshards.events;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import dev.conderfix.cfshards.utils.InvChecker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitListener implements Listener {

    public static boolean isShard(ItemStack item) {
        if (item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(
                NamespacedKey.fromString("shmatok"),
                PersistentDataType.STRING)) {
            return true;
        }
        return false;
    }

    private boolean isBlockedInventoryType(Inventory inv) {
        return Main.getInstance().getConfig().getStringList("block-inv").contains(inv.getType().toString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (InvChecker.isPlayerHadShmatok(player)) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("shmatok"), PersistentDataType.STRING)) {
                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
                    player.getInventory().remove(item);
                }
            }

            if (offHandItem != null && offHandItem.getItemMeta() != null && offHandItem.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("shmatok"), PersistentDataType.STRING)) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null || e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        Inventory topInv = e.getView().getTopInventory();
        ItemStack item = e.getCurrentItem();

        if (topInv == null || !Main.getInstance().getConfig().getStringList("block-inv").contains(topInv.getType().toString())) {
            return;
        }

        if ((e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) &&
                e.getHotbarButton() >= 0 && e.getHotbarButton() <= 9) {
            item = player.getInventory().getItem(e.getHotbarButton());
        }

        if (!isShard(item) || (topInv.contains(item) && e.getClickedInventory().getType() == InventoryType.ENDER_CHEST)) {
            return;
        }

        String message = HexUtil.translate(Main.getInstance().getConfig().getString("messages.no-storage"));
        player.sendMessage(message);
        e.setCancelled(true);
    }


    @EventHandler
    public void onLeftHandMove(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory topInv = e.getView().getTopInventory();
        if (topInv == null || !Main.getInstance().getConfig().getStringList("block-inv").contains(topInv.getType().toString())) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        if (e.getClick() == ClickType.SWAP_OFFHAND) {
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            if (offHandItem != null && isShard(offHandItem)) {
                e.setCancelled(true);
                String message = HexUtil.translate(Main.getInstance().getConfig().getString("messages.no-storage"));
                player.sendMessage(message);
            }
        }
    }


    @EventHandler
    public void onShmatokPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.getType() == Material.PLAYER_HEAD) {
            if (isShard(item)) {
                e.setCancelled(true);
                if (Main.getInstance().getConfig().getBoolean("shards.place-message.enable")) {
                    e.getPlayer().sendMessage(HexUtil.translate(Main.getInstance().getConfig().getString("shards.place-message.text")));
                }
            }
        }
    }


    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        Block block = event.getInventory().getLocation().getBlock();

        if (block.getType() == Material.HOPPER && isShard(event.getItem().getItemStack())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onHopper(InventoryPickupItemEvent e) {
        if (e.getInventory().getType() == InventoryType.HOPPER && isShard(e.getItem().getItemStack())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvMove(InventoryMoveItemEvent e) {
        ItemStack item = e.getItem();
        if (item != null && isShard(item)) {
            Inventory destInv = e.getDestination();
            if (destInv != null && isBlockedInventoryType(destInv) && e.getInitiator().getType() == InventoryType.HOPPER) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World destinationWorld = event.getTo().getWorld();

        List<String> exceptionWorlds = Main.getInstance().getConfig().getStringList("teleport.exception");
        for (String worldName : exceptionWorlds) {
            if (player.getInventory().contains(Material.PLAYER_HEAD) && worldName.equals(destinationWorld.getName())) {
                ItemStack playerHead = player.getInventory().getItem(player.getInventory().first(Material.PLAYER_HEAD));
                if (playerHead != null && isShard(playerHead)) {
                    event.setCancelled(true);
                    player.sendMessage(HexUtil.translate(Main.getInstance().getConfig().getString("teleport.message")));
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntityType() == EntityType.DROPPED_ITEM) {
            Item droppedItem = (Item) event.getEntity();
            if (droppedItem != null && isShard(droppedItem.getItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent event) {
        if (event.getView() == null || !(event.getView().getTopInventory().getHolder() instanceof Entity)) {
            return;
        }

        Entity entity = (Entity) event.getView().getTopInventory().getHolder();
        Player player = (Player) event.getWhoClicked();

        if (entity.getType() == EntityType.MINECART_CHEST ||
                entity.getType() == EntityType.MINECART_HOPPER ||
                entity.getType() == EntityType.MINECART_FURNACE ||
                entity.getType() == EntityType.MINECART ||
                entity.getType() == EntityType.HORSE ||
                entity.getType() == EntityType.MULE) {

            ItemStack item = event.getCurrentItem();
            if (event.getAction() == InventoryAction.HOTBAR_SWAP ||
                    event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
                int hotbarButton = event.getHotbarButton();
                if (hotbarButton >= 0 && hotbarButton <= 9) {
                    item = player.getInventory().getItem(hotbarButton);
                }
            }
            if (isShard(item)) {
                event.setCancelled(true);
                player.sendMessage(HexUtil.translate(Main.getInstance().getConfig().getString("messages.no-storage")));
            }
        }
    }

}