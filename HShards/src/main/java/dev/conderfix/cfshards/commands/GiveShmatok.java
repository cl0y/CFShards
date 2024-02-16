package dev.conderfix.cfshards.commands;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import dev.conderfix.cfshards.utils.ShardActions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GiveShmatok implements CommandExecutor {

    public static ShardActions shardActions = new ShardActions();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender == null){
            return false;
        }
        if (!commandSender.hasPermission("cfshards.admin")) {
            commandSender.sendMessage(
                    HexUtil.translate(Main.getInstance().getConfig().getString("messages.noPermission")));
            return true;
        }
        if (strings[0].equals("reload")) {
            commandSender.sendMessage("Done!");
            Main.getInstance().reloadConfig();
            return true;
        }
        if (strings[0].equals("give")) {
            String playerName = strings[1];
            int amout = Integer.parseInt(strings[2]);


            if (amout <= 0) {
                commandSender.sendMessage(HexUtil.translate("&cУкажите корректное число."));
                return true;
            }

            if (Bukkit.getPlayer(playerName) != null) {
                Player nick = Main.getInstance().getServer().getPlayer(playerName);
                shardActions.giveShmatoks(nick, amout);
                return true;
            }
            commandSender.sendMessage(HexUtil.translate("&сИгрок не найден."));
            return true;
        }
        if (strings[0].equals("remove")) {
            String playerName = strings[1];
            int amout = Integer.parseInt(strings[2]);


            if (amout <= 0) {
                commandSender.sendMessage(HexUtil.translate("&cУкажите корректное число."));
                return true;
            }

            if (Bukkit.getPlayer(playerName) != null) {
                Player nick = Main.getInstance().getServer().getPlayer(playerName);
                shardActions.takeShmatoks(nick, amout);
                return true;
            }
            commandSender.sendMessage(HexUtil.translate("&сИгрок не найден."));
        }
        return true;
    }

    
}
