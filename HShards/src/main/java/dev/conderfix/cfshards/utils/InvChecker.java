package dev.conderfix.cfshards.utils;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InvChecker {

    static List<Player> players = new ArrayList<>();

    static String color = Main.getInstance().getConfig().getString("shards.bossbar.color");
    static String style = Main.getInstance().getConfig().getString("shards.bossbar.style");
    static BossBar bossBar = Bukkit.createBossBar(HexUtil.translate(Main.getInstance().getConfig().getString("shards.bossbar.text")), BarColor.valueOf(color), BarStyle.valueOf(style));

    static void removeBossBar(Player player) {
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }

    static void removeFromPlayers(Player player) {
        if (players.contains(player)) {
            players.remove(player);
        }
    }

    public static boolean isPlayerHadShmatok(Player player) {
        if (players.contains(player)) {
            return true;
        } else {
            return false;
        }
    }

    public static void startInvChecking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item == null || item.getItemMeta() == null) {
                            continue;
                        }
                        if (item.getItemMeta().getPersistentDataContainer().has(
                                NamespacedKey.fromString("shmatok"),
                                PersistentDataType.STRING)) {
                            boolean bar24 = Main.getInstance().getConfig().getBoolean("shards.bossbar.enable");
                            if (bar24) {
                                bossBar.addPlayer(p);
                            }
                            boolean act = Main.getInstance().getConfig().getBoolean("shards.actionbar.enable");
                            if (act) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(HexUtil.translate(
                                        Main.getInstance().getConfig().getString("shards.actionbar.text"))));
                            }
                            boolean tit = Main.getInstance().getConfig().getBoolean("shards.title.enable");
                            if (tit) {
                                String one = HexUtil.translate(Main.getInstance().getConfig().getString("shards.title.text"));
                                String two = HexUtil.translate(Main.getInstance().getConfig().getString("shards.title.subtitle"));
                                p.sendTitle(one, two, 10, 20, 10);
                            }
                            if (!players.contains(p)) {
                                players.add(p);
                            }
                            return;
                        }
                    }
                    removeBossBar(p);
                    removeFromPlayers(p);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
