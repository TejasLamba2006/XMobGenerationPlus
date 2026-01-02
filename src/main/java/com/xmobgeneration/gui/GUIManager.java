package com.xmobgeneration.gui;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.gui.menus.CustomDropsMenu;
import com.xmobgeneration.gui.menus.CustomMobEquipmentMenu;
import com.xmobgeneration.gui.menus.MobStatsMenu;
import com.xmobgeneration.gui.menus.MobTypeMenu;
import com.xmobgeneration.models.SpawnArea;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GUIManager {
    private final XMobGeneration plugin;
    private static final int GUI_SIZE = 54;
    private final CustomDropsMenu customDropsMenu;
    private final MobTypeMenu mobTypeMenu;

    public GUIManager(XMobGeneration plugin) {
        this.plugin = plugin;
        this.customDropsMenu = new CustomDropsMenu(plugin);
        this.mobTypeMenu = new MobTypeMenu(plugin);
    }

    public void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8XMobGenerationPlus");

        ItemStack createButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName("§aCreate New Area");
        List<String> createLore = new ArrayList<>();
        createLore.add("§7Click to create a new spawn area");
        createLore.add("§7Areas: §f" + plugin.getAreaManager().getAllAreas().size() + "/"
                + plugin.getAreaManager().getMaxAreas());
        createLore.add("");
        createLore.add("§eRequires WorldEdit selection!");
        createMeta.setLore(createLore);
        createButton.setItemMeta(createMeta);
        gui.setItem(11, createButton);

        ItemStack editButton = new ItemStack(Material.ANVIL);
        ItemMeta editMeta = editButton.getItemMeta();
        editMeta.setDisplayName("§6Edit Area");
        editMeta.setLore(Arrays.asList("§7Click to edit an existing area"));
        editButton.setItemMeta(editMeta);
        gui.setItem(13, editButton);

        ItemStack deleteButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta deleteMeta = deleteButton.getItemMeta();
        deleteMeta.setDisplayName("§cDelete Area");
        deleteMeta.setLore(Arrays.asList("§7Click to delete an area"));
        deleteButton.setItemMeta(deleteMeta);
        gui.setItem(15, deleteButton);

        player.openInventory(gui);
    }

    public void openAreaEditGUI(Player player, SpawnArea area) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, "§8Edit Area - " + area.getName());

        String mobTypeDisplay = area.isMythicMob() ? "§d" + area.getMythicMobType() : "§f" + area.getMobType();
        ItemStack mobType = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta mobTypeMeta = mobType.getItemMeta();
        mobTypeMeta.setDisplayName("§eMob Type: " + mobTypeDisplay);
        List<String> mobTypeLore = new ArrayList<>();
        mobTypeLore.add("§7Click to change mob type");
        if (area.isMythicMob()) {
            mobTypeLore.add("§7Type: §dMythicMob");
        }
        mobTypeMeta.setLore(mobTypeLore);
        mobType.setItemMeta(mobTypeMeta);
        gui.setItem(10, mobType);

        ItemStack spawnCount = new ItemStack(Material.REPEATER);
        ItemMeta spawnCountMeta = spawnCount.getItemMeta();
        spawnCountMeta.setDisplayName("§eSpawn Count: §f" + area.getSpawnCount());
        List<String> spawnLore = new ArrayList<>();
        spawnLore.add("§7Left-Click: §a+1");
        spawnLore.add("§7Right-Click: §c-1");
        spawnLore.add("§7Shift+Left: §a+10");
        spawnLore.add("§7Shift+Right: §c-10");
        spawnCountMeta.setLore(spawnLore);
        spawnCount.setItemMeta(spawnCountMeta);
        gui.setItem(12, spawnCount);

        ItemStack respawnDelay = new ItemStack(Material.CLOCK);
        ItemMeta respawnDelayMeta = respawnDelay.getItemMeta();
        respawnDelayMeta.setDisplayName("§eRespawn Delay: §f" + area.getRespawnDelay() + "s");
        List<String> delayLore = new ArrayList<>();
        delayLore.add("§7Left-Click: §a+5s");
        delayLore.add("§7Right-Click: §c-5s");
        delayLore.add("§7Shift+Left: §a+30s");
        delayLore.add("§7Shift+Right: §c-30s");
        respawnDelayMeta.setLore(delayLore);
        respawnDelay.setItemMeta(respawnDelayMeta);
        gui.setItem(14, respawnDelay);

        ItemStack proximityBtn = new ItemStack(
                area.isPlayerProximityRequired() ? Material.ENDER_EYE : Material.ENDER_PEARL);
        ItemMeta proxMeta = proximityBtn.getItemMeta();
        proxMeta.setDisplayName(
                "§ePlayer Proximity: " + (area.isPlayerProximityRequired() ? "§aEnabled" : "§cDisabled"));
        List<String> proxLore = new ArrayList<>();
        proxLore.add("§7Range: §b" + area.getProximityRange() + " blocks");
        proxLore.add("");
        proxLore.add("§7Left-Click: §aToggle");
        proxLore.add("§7Right-Click: §e+10 range");
        proxLore.add("§7Shift+Right: §e-10 range");
        proxMeta.setLore(proxLore);
        proximityBtn.setItemMeta(proxMeta);
        gui.setItem(16, proximityBtn);

        ItemStack xpBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpMeta = xpBottle.getItemMeta();
        xpMeta.setDisplayName("§eXP Amount: §b" + area.getXpAmount());
        List<String> xpLore = new ArrayList<>();
        xpLore.add("§7Left-Click: §a+10 XP");
        xpLore.add("§7Right-Click: §c-10 XP");
        xpLore.add("§7Shift+Left: §a+50 XP");
        xpLore.add("§7Shift+Right: §c-50 XP");
        xpMeta.setLore(xpLore);
        xpBottle.setItemMeta(xpMeta);
        gui.setItem(28, xpBottle);

        ItemStack customDrops = new ItemStack(Material.CHEST);
        ItemMeta customDropsMeta = customDrops.getItemMeta();
        customDropsMeta.setDisplayName("§eCustom Drops");
        List<String> customDropsLore = new ArrayList<>();
        customDropsLore.add("§7Click to configure custom drops");
        customDropsLore.add("");
        customDropsLore.add(area.getCustomDrops().isEnabled() ? "§aEnabled" : "§cDisabled");
        customDropsLore.add("§7Items: §f" + area.getCustomDrops().getItems().size());
        customDropsMeta.setLore(customDropsLore);
        customDrops.setItemMeta(customDropsMeta);
        gui.setItem(30, customDrops);

        ItemStack mobStats = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta mobStatsMeta = mobStats.getItemMeta();
        mobStatsMeta.setDisplayName("§eMob Stats");
        List<String> mobStatsLore = new ArrayList<>();
        mobStatsLore.add("§7Click to configure mob stats");
        mobStatsLore.add("");
        mobStatsLore.add("§7Health: §c" + area.getMobStats().getHealth() + "❤");
        mobStatsLore.add("§7Damage: §e" + area.getMobStats().getDamage());
        mobStatsLore.add("§7Level: §b" + area.getMobStats().getLevel());
        mobStatsLore.add("§7Name: §f" + area.getMobStats().getMobName());
        mobStatsMeta.setLore(mobStatsLore);
        mobStats.setItemMeta(mobStatsMeta);
        gui.setItem(32, mobStats);

        ItemStack equipmentButton = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta equipmentMeta = equipmentButton.getItemMeta();
        equipmentMeta.setDisplayName("§eMob Equipment");
        List<String> equipmentLore = new ArrayList<>();
        equipmentLore.add("§7Click to configure mob equipment");
        equipmentMeta.setLore(equipmentLore);
        equipmentButton.setItemMeta(equipmentMeta);
        gui.setItem(34, equipmentButton);

        ItemStack levelRange = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta levelMeta = levelRange.getItemMeta();
        levelMeta.setDisplayName("§eLevel Range: §b" + area.getMinLevel() + " - " + area.getMaxLevel());
        List<String> levelLore = new ArrayList<>();
        levelLore.add("§7Left-Click: §a+1 min level");
        levelLore.add("§7Right-Click: §c-1 min level");
        levelLore.add("§7Shift+Left: §a+1 max level");
        levelLore.add("§7Shift+Right: §c-1 max level");
        levelMeta.setLore(levelLore);
        levelRange.setItemMeta(levelMeta);
        gui.setItem(22, levelRange);

        ItemStack bossToggle = new ItemStack(area.isBossArea() ? Material.DRAGON_HEAD : Material.SKELETON_SKULL);
        ItemMeta bossMeta = bossToggle.getItemMeta();
        bossMeta.setDisplayName("§eBoss Area: " + (area.isBossArea() ? "§aEnabled" : "§cDisabled"));
        List<String> bossLore = new ArrayList<>();
        bossLore.add("§7Click to toggle boss area mode");
        if (area.isBossArea() && area.getBossSpawnPoint() != null) {
            bossLore.add("§7Spawn Point: §aSet");
        } else if (area.isBossArea()) {
            bossLore.add("§cNo spawn point set!");
            bossLore.add("§7Use /xmg getwand");
        }
        bossMeta.setLore(bossLore);
        bossToggle.setItemMeta(bossMeta);
        gui.setItem(40, bossToggle);

        ItemStack toggleButton = new ItemStack(area.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta toggleMeta = toggleButton.getItemMeta();
        toggleMeta.setDisplayName(area.isEnabled() ? "§aSpawning Enabled" : "§cSpawning Disabled");
        toggleMeta.setLore(Arrays.asList("§7Click to toggle spawning"));
        toggleButton.setItemMeta(toggleMeta);
        gui.setItem(49, toggleButton);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c← Back to Area List");
        backButton.setItemMeta(backMeta);
        gui.setItem(45, backButton);

        ItemStack restartArea = new ItemStack(Material.REDSTONE);
        ItemMeta restartMeta = restartArea.getItemMeta();
        restartMeta.setDisplayName("§cRestart Area");
        restartMeta.setLore(Arrays.asList("§7Click to despawn all mobs", "§7and restart spawning"));
        restartArea.setItemMeta(restartMeta);
        gui.setItem(53, restartArea);

        player.openInventory(gui);
    }

    public void openMobTypeMenu(Player player, SpawnArea area, int page) {
        mobTypeMenu.open(player, area, page);
    }

    public MobTypeMenu getMobTypeMenu() {
        return mobTypeMenu;
    }

    public void openCustomMobEquipmentMenu(Player player, SpawnArea area) {
        new CustomMobEquipmentMenu(plugin).openMenu(player, area);
    }

    public void openCustomDropsMenu(Player player, SpawnArea area) {
        customDropsMenu.openMenu(player, area);
    }

    public CustomDropsMenu getCustomDropsMenu() {
        return customDropsMenu;
    }

    public void openMobStatsMenu(Player player, SpawnArea area) {
        new MobStatsMenu(plugin).openMenu(player, area);
    }

    public void openAreaListGUI(Player player, String action) {
        Map<String, SpawnArea> areas = plugin.getAreaManager().getAllAreas();
        int totalAreas = areas.size();
        int guiSize = Math.min(54, Math.max(27, ((totalAreas / 9) + 1) * 9 + 9));

        Inventory gui = Bukkit.createInventory(null, guiSize, "§8Areas - " + action);

        int slot = 0;
        for (SpawnArea area : areas.values()) {
            if (slot >= guiSize - 9)
                break;

            ItemStack item;
            if (action.equals("list") || action.equals("edit")) {
                item = new ItemStack(
                        area.isEnabled() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
            } else {
                item = new ItemStack(Material.PAPER);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + area.getName());

            List<String> lore = new ArrayList<>();
            String mobType = area.isMythicMob() ? "§d" + area.getMythicMobType() : "§f" + area.getMobType();
            lore.add("§7Mob Type: " + mobType);
            lore.add("§7Spawn Count: §f" + area.getSpawnCount());
            lore.add("§7Respawn Delay: §f" + area.getRespawnDelay() + "s");
            lore.add("§7XP: §b" + area.getXpAmount());
            if (area.isBossArea()) {
                lore.add("§6Boss Area");
            }
            lore.add("");
            lore.add(area.isEnabled() ? "§aEnabled" : "§cDisabled");
            if (action.equals("edit")) {
                lore.add("§7Click to edit");
            } else if (action.equals("delete")) {
                lore.add("§cClick to delete");
            } else if (action.equals("list")) {
                lore.add("§7Click to toggle spawning");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(slot++, item);
        }

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c← Back to Main Menu");
        backButton.setItemMeta(backMeta);
        gui.setItem(guiSize - 5, backButton);

        player.openInventory(gui);
    }
}