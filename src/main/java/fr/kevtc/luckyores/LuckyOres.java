package fr.kevtc.luckyores;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class LuckyOres extends JavaPlugin {

    @Override
    public void onEnable() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()){
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            saveConfig();
        }
        getServer().getPluginManager().registerEvents(new OresBreak(this), this);
        getLogger().info("LuckyOres enabled");

    }

    @Override
    public void onDisable() {
        getLogger().info("LuckyOres Disabled");
    }
}
