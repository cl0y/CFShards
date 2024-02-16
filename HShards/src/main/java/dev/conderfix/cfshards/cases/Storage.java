package dev.conderfix.cfshards.cases;

import dev.conderfix.cfshards.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Storage {

    private static FileConfiguration caseConfig;

    private static File file;

    public Storage(String name) {
        file = new File(Main.getInstance().getDataFolder(), name);
        try {
            if (!file.exists() && !file.createNewFile()) throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create {name}".replace("{name}", name), e);
        }
        caseConfig = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getCaseConfig() {
        return caseConfig;
    }

    public void saveCaseConfig() {
        try {
            caseConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isThisBlockCase(Location location) {
        int xx = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.x"));
        int yy = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.y"));
        int zz = Integer.parseInt(Main.getInstance().caseConfig.getString("case-location.z"));

        Location caseLocation = new Location(Bukkit.getWorld(Main.getInstance().caseConfig.getString("case-location.world")), xx, yy, zz);
        if (location.equals(caseLocation)) {
            return true;
        } else {
            return false;
        }
    }
}
