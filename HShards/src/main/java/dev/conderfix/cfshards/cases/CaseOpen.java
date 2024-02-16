package dev.conderfix.cfshards.cases;

import dev.conderfix.cfshards.Main;
import dev.conderfix.cfshards.utils.HexUtil;
import dev.conderfix.cfshards.utils.ShardActions;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseOpen {

    public static ShardActions shardActions = new ShardActions();
    public static void spawnFirework(Color color) {
        int xx = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.x"));
        int yy = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.y"));
        int zz = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.z"));

        World world = Bukkit.getWorld(Main.getInstance().caseConfig.getString("case-location.world"));
        Location caseLocation = new Location(world, xx + 0.5, yy + 1.0, zz + 0.5);

        Firework firework = caseLocation.getWorld().spawn(caseLocation, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.BALL).trail(true).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    private static String cmd = Main.getInstance().caseConfig.getString("case-open.give-cmd");

    public static boolean isPlayerHaveEnough(Player player) {
        int requiredAmount = Main.getInstance().caseConfig.getInt("case-open.take-shards");
        int totalAmount = getPlayerShmatoks(player);
        if (totalAmount >= requiredAmount) {
            return true;
        } else {
            player.sendMessage(HexUtil.translate(Main.getInstance().caseConfig.getString("case-open.messages.no-shards")));
            return false;
        }
    }

    public static int getPlayerShmatoks(Player player) {
        int amount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("shmatok"), PersistentDataType.STRING)) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    private static List<String> messages;

    private int value;
    private int chance;

    public CaseOpen(int value, int chance) {
        this.value = value;
        this.chance = chance;
    }

    public int getValue() {
        return value;
    }

    public int getChance() {
        return chance;
    }

    public static void getPrize(Player player) {
        boolean one = Main.getInstance().caseConfig.getBoolean("prizes.type-one.enable");
        if (one) {
            List<String> stringValues = Main.getInstance().caseConfig.getStringList("prizes.type-one.list");
            List<CaseOpen> prizeList = new ArrayList<>();

            for (String stringValue : stringValues) {
                String[] parts = stringValue.split(";");
                int value = Integer.parseInt(parts[0]);
                int chance = Integer.parseInt(parts[1]);
                prizeList.add(new CaseOpen(value, chance));
            }

            Random random = new Random();
            if (!prizeList.isEmpty()) {
                CaseOpen selectedPrize = prizeList.get(random.nextInt(prizeList.size()));
                int prize = selectedPrize.getValue();

                player.sendTitle(HexUtil.translate(Main.getInstance().caseConfig.getString("case-open.title").replace("{prize}", Integer.toString(prize))), "", 20, 10, 20);

                String nick = player.getName();
                messages = Main.getInstance().caseConfig.getStringList("case-open.messages.winning");

                String prize_command = cmd.replace("{player}", nick);
                prize_command = prize_command.replace("{prize}", String.valueOf(prize));

                boolean fgh = Main.getInstance().caseConfig.getBoolean("case-open.open-message.enable");
                if (fgh) {
                    player.sendMessage(HexUtil.translate(Main.getInstance().caseConfig.getString("case-open.open-message.text")));
                }

                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    for (String line : messages) {
                        line = line.replace("{player}", nick);
                        line = line.replace("{prize}", String.valueOf(prize));
                        player2.sendMessage(HexUtil.translate(line));
                    }
                }


                System.out.println(prize_command);

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prize_command);
            }
        }

        boolean two = Main.getInstance().caseConfig.getBoolean("prizes.type-two.enable");
        if (two) {

            int min = Main.getInstance().caseConfig.getInt("prizes.type-two.min-prize");
            int mix = Main.getInstance().caseConfig.getInt("prizes.type-two.max-prize");
            int prize = new Random().nextInt(mix) + min;

                player.sendTitle(HexUtil.translate(Main.getInstance().caseConfig.getString("case-open.title").replace("{prize}", Integer.toString(prize))), "", 20, 10, 20);

                String nick = player.getName();
                messages = Main.getInstance().caseConfig.getStringList("case-open.messages.winning");

                String prize_command = cmd.replace("{player}", nick);
                prize_command = prize_command.replace("{prize}", String.valueOf(prize));

                boolean fgh = Main.getInstance().caseConfig.getBoolean("case-open.open-message.enable");
                if (fgh) {
                    player.sendMessage(HexUtil.translate(Main.getInstance().caseConfig.getString("case-open.open-message.text")));
                }

                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    for (String line : messages) {
                        line = line.replace("{player}", nick);
                        line = line.replace("{prize}", String.valueOf(prize));
                        player2.sendMessage(HexUtil.translate(line));
                    }
                }


                System.out.println(prize_command);

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prize_command);

            }
        }

    public static void openCase(Player player) {
        if (isPlayerHaveEnough(player)) {
            // case
            int xx = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.x"));
            int yy = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.y"));
            int zz = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.z"));

            Location caseLocation = new Location(Bukkit.getWorld(Main.getInstance().caseConfig.getString("case-location.world")), xx, yy, zz);
            // case
            shardActions.takeShmatoks(player, Main.getInstance().caseConfig.getInt("case-open.take-shards"));
            getPrize(player);
            boolean firework = Main.getInstance().caseConfig.getBoolean("case-open.firework");
            if (firework) {
                int one = Main.getInstance().caseConfig.getInt("case-open.color-firework.red");
                int green = Main.getInstance().caseConfig.getInt("case-open.color-firework.green");
                int blue = Main.getInstance().caseConfig.getInt("case-open.color-firework.blue");
                spawnFirework(Color.fromRGB(one, green, blue));
            }
        }
    }
}
