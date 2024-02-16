package dev.conderfix.cfshards.cases;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class gui {

    Inventory inventory;

    public gui() {
        int size = Main.getInstance().caseConfig.getInt("inv.size");
        String title = HexUtil.translate(Main.getInstance().caseConfig.getString("inv.name"));
        inventory = Bukkit.createInventory(null, size, title);
    }

    public void addItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void loadDecorations() {
        List<Integer> slots = Main.getInstance().caseConfig.getIntegerList("inv.decor.slots");

        String materialName = Main.getInstance().caseConfig.getString("inv.decor.type");
        Material material = Material.valueOf(materialName);
        ItemStack redGlass = new ItemStack(material);
        ItemMeta glassItemMeta = redGlass.getItemMeta();
        glassItemMeta.setDisplayName(HexUtil.translate("&c"));

        for (int i : slots) {
            ItemStack redGlassClone = redGlass.clone();
            redGlassClone.setItemMeta(glassItemMeta);
            addItem(i, redGlassClone);
        }

        String chestName = Main.getInstance().caseConfig.getString("inv.case.name");
        Material chestMaterial = Material.valueOf(Main.getInstance().caseConfig.getString("inv.case.type"));
        ItemStack chest = new ItemStack(chestMaterial);
        ItemMeta chestItemMeta = chest.getItemMeta();
        chestItemMeta.setDisplayName(HexUtil.translate(chestName));

        List<String> chestLore = Main.getInstance().caseConfig.getStringList("inv.case.lore");
        List<String> translatedLore = chestLore.stream()
                .map(HexUtil::translate)
                .collect(Collectors.toList());
        chestItemMeta.setLore(translatedLore);
        chestItemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("caseopen"), PersistentDataType.STRING, "true");
        chest.setItemMeta(chestItemMeta);

        addItem(Main.getInstance().caseConfig.getInt("inv.case.slot"), chest);
    }

    public Inventory getInventory() {
        loadDecorations();
        return inventory;
    }
}
