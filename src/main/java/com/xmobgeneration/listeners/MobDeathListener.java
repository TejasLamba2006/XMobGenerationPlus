package com.xmobgeneration.listeners;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.CustomDrops;
import com.xmobgeneration.models.SpawnArea;
import com.xmobgeneration.models.SpawnedMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MobDeathListener implements Listener {
    private final XMobGeneration plugin;
    private final Random random = new Random();

    public MobDeathListener(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        SpawnedMob spawnedMob = plugin.getSpawnManager().getMobTracker().getMob(entity.getUniqueId());
        if (spawnedMob == null)
            return;

        SpawnArea area = plugin.getAreaManager().getArea(spawnedMob.getAreaName());
        if (area == null)
            return;

        handleCustomDrops(event, area);

        if (killer != null) {
            handleXPDistribution(entity, killer, area);
        }

        plugin.getSpawnManager().handleMobDeath(entity);
    }

    private void handleCustomDrops(EntityDeathEvent event, SpawnArea area) {
        CustomDrops customDrops = area.getCustomDrops();
        if (!customDrops.isEnabled())
            return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        for (int i = 0; i < customDrops.getItems().size(); i++) {
            ItemStack item = customDrops.getItems().get(i);
            double chance = customDrops.getChances().get(i);

            if (random.nextDouble() * 100 <= chance) {
                event.getDrops().add(item.clone());
            }
        }
    }

    private void handleXPDistribution(LivingEntity entity, Player killer, SpawnArea area) {
        if (entity.hasMetadata("isBoss")) {
            plugin.getXPManager().distributeBossXP(entity.getUniqueId(), area.getXpAmount());
        } else {
            plugin.getXPManager().awardXP(killer, area.getXpAmount());
        }
    }
}