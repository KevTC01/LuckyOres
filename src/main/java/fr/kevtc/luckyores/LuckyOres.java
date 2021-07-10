package fr.kevtc.luckyores;

import org.bukkit.plugin.java.JavaPlugin;

public final class LuckyOres extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        getServer().getPluginManager().registerEvents(new OresBreak(this), this);
        getLogger().info("LuckyOres enabled");

    }

    @Override
    public void onDisable() {
        getLogger().info("LuckyOres Disabled");
    }
}
