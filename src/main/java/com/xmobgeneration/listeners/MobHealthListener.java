package com.xmobgeneration.listeners;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.SpawnArea;
import com.xmobgeneration.models.SpawnedMob;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthListener implements Listener {
    private final XMobGeneration plugin;

    public MobHealthListener(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        SpawnedMob spawnedMob = plugin.getSpawnManager().getMobTracker().getMob(entity.getUniqueId());

        if (spawnedMob == null)
            return;

        SpawnArea area = plugin.getAreaManager().getArea(spawnedMob.getAreaName());
        if (area == null || !area.getMobStats().isShowName())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead())
                    return;
                updateHealthDisplayInstance(entity, area);
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        SpawnedMob spawnedMob = plugin.getSpawnManager().getMobTracker().getMob(entity.getUniqueId());

        if (spawnedMob == null)
            return;

        SpawnArea area = plugin.getAreaManager().getArea(spawnedMob.getAreaName());
        if (area == null || !area.getMobStats().isShowName())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead())
                    return;
                updateHealthDisplayInstance(entity, area);
            }
        }.runTaskLater(plugin, 1L);
    }

    private void updateHealthDisplayInstance(LivingEntity entity, SpawnArea area) {
        updateHealthDisplay(entity, area.getMobStats().getLevel(), area.getMobStats().getMobName());
    }

    public static void updateHealthDisplay(LivingEntity entity, int level, String mobName) {
        double currentHealth = entity.getHealth();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        String healthBar = createHealthBar(currentHealth, maxHealth);
        String displayName = String.format("§7[Lv.%d] §f%s %s §c%.1f§7/§c%.1f❤",
                level, mobName, healthBar, currentHealth, maxHealth);

        entity.setCustomName(displayName);
        entity.setCustomNameVisible(true);
    }

    private static String createHealthBar(double current, double max) {
        int totalBars = 10;
        int filledBars = (int) Math.ceil((current / max) * totalBars);
        filledBars = Math.max(0, Math.min(totalBars, filledBars));

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                if (filledBars <= 3) {
                    bar.append("§c|");
                } else if (filledBars <= 6) {
                    bar.append("§e|");
                } else {
                    bar.append("§a|");
                }
            } else {
                bar.append("§7|");
            }
        }
        bar.append("§8]");
        return bar.toString();
    }

    public void applyInitialHealthDisplay(LivingEntity entity, SpawnArea area) {
        if (area.getMobStats().isShowName()) {
            updateHealthDisplay(entity, area.getMobStats().getLevel(), area.getMobStats().getMobName());
        }
    }
}