package dev.conderfix.cfshards;

import dev.conderfix.cfshards.cases.Events;
import dev.conderfix.cfshards.cases.Storage;
import dev.conderfix.cfshards.commands.GiveShmatok;
import dev.conderfix.cfshards.events.BukkitListener;
import dev.conderfix.cfshards.utils.InvChecker;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance()  {
        return instance;
    }
    public FileConfiguration caseConfig;
    public Storage caseStorage;

    public Storage getCaseStorage() {
        return instance.caseStorage;
    }

    public void loadCaseConfig() {
        saveResource("case.yml", false);
        File file = new File(getDataFolder().getAbsoluteFile() + "/case.yml");
        caseConfig = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        loadCaseConfig();
        InvChecker.startInvChecking();
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        getCommand("cfshards").setExecutor(new GiveShmatok());
        caseStorage = new Storage("case.yml");
    }

}
