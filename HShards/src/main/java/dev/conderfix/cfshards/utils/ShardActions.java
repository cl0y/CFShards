package dev.conderfix.cfshards.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.conderfix.cfshards.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShardActions {

    public void giveShmatoks(Player player, int amount) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(HexUtil.translate(Main.getInstance().getConfig().getString("shards.name")));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("shmatok"), PersistentDataType.STRING, "");

        List<String> lore = Main.getInstance().getConfig().getStringList("shards.lore");
        List<String> translatedLore = lore.stream().map(HexUtil::translate).collect(Collectors.toList());
        meta.setLore(translatedLore);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Main.getInstance().getConfig().getString("shards.texture")));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    public void takeShmatoks(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];

            if (stack != null && stack.getItemMeta() != null && stack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("shmatok"), PersistentDataType.STRING)) {
                int stackAmount = stack.getAmount();

                if (stackAmount <= amount) {
                    player.getInventory().setItem(i, null);
                    amount -= stackAmount;
                } else {
                    stack.setAmount(stackAmount - amount);
                    break;
                }
            }
        }
    }


}
