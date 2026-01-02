package com.xmobgeneration.config;

import com.xmobgeneration.XMobGeneration;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final XMobGeneration plugin;
    private FileConfiguration config;

    public ConfigManager(XMobGeneration plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Message not found: " + path)
                .replace("&", "§");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isMobContainmentEnabled() {
        return config.getBoolean("settings.mob-containment-enabled", true);
    }

    public boolean doPassiveMobsAttackPlayers() {
        return config.getBoolean("settings.passive-mobs-attack-players", true);
    }

    public int getHealthUpdateInterval() {
        return config.getInt("settings.health-update-interval", 10);
    }
}