package com.xmobgeneration.managers;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.MobStats;
import com.xmobgeneration.models.SpawnArea;
import com.xmobgeneration.models.SpawnedMob;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {
    private final XMobGeneration plugin;
    private final Map<UUID, BossBar> mobBossBars = new HashMap<>();
    private final Map<UUID, String> mobAreaNames = new HashMap<>();
    private BukkitRunnable updateTask;

    public BossBarManager(XMobGeneration plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAllBossBars();
            }
        };
        updateTask.runTaskTimer(plugin, 5L, 5L);
    }

    public void createBossBar(LivingEntity entity, SpawnArea area) {
        if (!area.getMobStats().isShowBossBar()) return;

        MobStats stats = area.getMobStats();
        String title = formatTitle(entity, stats, area.getRandomLevel());
        
        BossBar bossBar = Bukkit.createBossBar(
            title,
            stats.getBarColor(),
            stats.getBarStyle()
        );
        
        bossBar.setVisible(stats.isBossBarVisible());
        bossBar.setProgress(1.0);
        
        mobBossBars.put(entity.getUniqueId(), bossBar);
        mobAreaNames.put(entity.getUniqueId(), area.getName());
    }

    public void removeBossBar(UUID entityId) {
        BossBar bossBar = mobBossBars.remove(entityId);
        mobAreaNames.remove(entityId);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void removeAllBossBars() {
        for (BossBar bossBar : mobBossBars.values()) {
            bossBar.removeAll();
        }
        mobBossBars.clear();
        mobAreaNames.clear();
    }

    public void removeAreaBossBars(String areaName) {
        mobAreaNames.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(areaName)) {
                BossBar bar = mobBossBars.remove(entry.getKey());
                if (bar != null) bar.removeAll();
                return true;
            }
            return false;
        });
    }

    private void updateAllBossBars() {
        for (Map.Entry<UUID, BossBar> entry : new HashMap<>(mobBossBars).entrySet()) {
            UUID entityId = entry.getKey();
            BossBar bossBar = entry.getValue();
            String areaName = mobAreaNames.get(entityId);
            
            if (areaName == null) {
                removeBossBar(entityId);
                continue;
            }
            
            SpawnedMob spawnedMob = plugin.getSpawnManager().getMobTracker().getMob(entityId);
            if (spawnedMob == null || spawnedMob.getEntity() == null || spawnedMob.getEntity().isDead()) {
                removeBossBar(entityId);
                continue;
            }
            
            SpawnArea area = plugin.getAreaManager().getArea(areaName);
            if (area == null) {
                removeBossBar(entityId);
                continue;
            }
            
            LivingEntity entity = (LivingEntity) spawnedMob.getEntity();
            MobStats stats = area.getMobStats();
            
            double currentHealth = entity.getHealth();
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double progress = Math.max(0, Math.min(1, currentHealth / maxHealth));
            
            bossBar.setProgress(progress);
            bossBar.setTitle(formatTitle(entity, stats, getEntityLevel(entity)));
            bossBar.setColor(stats.getBarColor());
            bossBar.setStyle(stats.getBarStyle());
            
            updateBossBarPlayers(entity, bossBar, stats.getBossBarRange());
        }
    }

    private void updateBossBarPlayers(LivingEntity entity, BossBar bossBar, int range) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(entity.getWorld())) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance <= range) {
                    if (!bossBar.getPlayers().contains(player)) {
                        bossBar.addPlayer(player);
                    }
                } else {
                    bossBar.removePlayer(player);
                }
            } else {
                bossBar.removePlayer(player);
            }
        }
    }

    private String formatTitle(LivingEntity entity, MobStats stats, int level) {
        double currentHealth = entity.getHealth();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        
        String healthColor;
        double healthPercent = currentHealth / maxHealth;
        if (healthPercent > 0.6) {
            healthColor = "§a";
        } else if (healthPercent > 0.3) {
            healthColor = "§e";
        } else {
            healthColor = "§c";
        }
        
        return String.format("§7[Lv.%d] §f%s §8| %s%.1f§7/§c%.1f §c❤",
            level, stats.getMobName(), healthColor, currentHealth, maxHealth);
    }

    private int getEntityLevel(LivingEntity entity) {
        if (entity.hasMetadata("mobLevel")) {
            return entity.getMetadata("mobLevel").get(0).asInt();
        }
        return 1;
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        removeAllBossBars();
    }
}
