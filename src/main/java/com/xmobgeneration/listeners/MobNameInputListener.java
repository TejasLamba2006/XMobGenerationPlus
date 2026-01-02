package com.xmobgeneration.listeners;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.SpawnArea;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MobNameInputListener implements Listener {
    private final XMobGeneration plugin;
    private final Player player;
    private final SpawnArea area;

    public MobNameInputListener(XMobGeneration plugin, Player player, SpawnArea area) {
        this.plugin = plugin;
        this.player = player;
        this.area = area;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player))
            return;

        event.setCancelled(true);
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage("§cMob name change cancelled.");
        } else {
            area.getMobStats().setMobName(message.replace("&", "§"));
            plugin.getAreaManager().saveAreas();
            player.sendMessage("§aMob name set to: " + message.replace("&", "§"));
        }

        HandlerList.unregisterAll(this);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getGUIManager().openMobStatsMenu(player, area);
        });
    }
}
