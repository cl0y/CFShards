package dev.conderfix.cfshards.cases;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class Events implements Listener {

    @EventHandler
    public void onCaseClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = e.getClickedBlock();
            Player player = e.getPlayer();
            Location location = block.getLocation();
            if (Main.getInstance().getCaseStorage().isThisBlockCase(location)) {
                gui caseGui = new gui();
                player.openInventory(caseGui.getInventory());
            }
        }
    }

    @EventHandler
    public void onCaseBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        if (Main.getInstance().getCaseStorage().isThisBlockCase(location)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCaseInvClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(HexUtil.translate(Main.getInstance().caseConfig.getString("inv.name")))) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("caseopen"), PersistentDataType.STRING)) {
                player.closeInventory();
                CaseOpen.openCase(player);
            }
        }
    }
}
