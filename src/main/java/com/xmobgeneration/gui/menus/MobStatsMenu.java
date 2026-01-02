package com.xmobgeneration.gui.menus;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.SpawnArea;
import com.xmobgeneration.utils.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class MobStatsMenu {
    private final XMobGeneration plugin;

    public MobStatsMenu(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, SpawnArea area) {
        Inventory gui = Bukkit.createInventory(null, 36, "§8Mob Stats - " + area.getName());

        ItemStack nameToggle = GuiUtils.createGuiItem(
                Material.NAME_TAG,
                area.getMobStats().isShowName() ? "§aMob Names Enabled" : "§cMob Names Disabled",
                Arrays.asList(
                        "§7Current Name: §f" + area.getMobStats().getMobName(),
                        "",
                        "§7Click to toggle name display",
                        "§eShift+Click to change name"));
        gui.setItem(10, nameToggle);

        List<String> healthLore = new ArrayList<>();
        healthLore.add("§7Current: §c" + area.getMobStats().getHealth() + "❤");
        healthLore.add("");
        healthLore.add("§7Left-Click: §a+5");
        healthLore.add("§7Right-Click: §c-5");
        healthLore.add("§7Shift+Left: §a+50");
        healthLore.add("§7Shift+Right: §c-50");
        ItemStack health = GuiUtils.createGuiItem(Material.RED_DYE, "§cMob Health", healthLore);
        gui.setItem(12, health);

        List<String> damageLore = new ArrayList<>();
        damageLore.add("§7Current: §e" + area.getMobStats().getDamage());
        damageLore.add("");
        damageLore.add("§7Left-Click: §a+1");
        damageLore.add("§7Right-Click: §c-1");
        damageLore.add("§7Shift+Left: §a+5");
        damageLore.add("§7Shift+Right: §c-5");
        ItemStack damage = GuiUtils.createGuiItem(Material.IRON_SWORD, "§eMob Damage", damageLore);
        gui.setItem(14, damage);

        List<String> levelLore = new ArrayList<>();
        levelLore.add("§7Current: §b" + area.getMobStats().getLevel());
        levelLore.add("");
        levelLore.add("§7Left-Click: §a+1");
        levelLore.add("§7Right-Click: §c-1");
        levelLore.add("§7Shift+Left: §a+10");
        levelLore.add("§7Shift+Right: §c-10");
        ItemStack level = GuiUtils.createGuiItem(Material.EXPERIENCE_BOTTLE, "§bMob Level", levelLore);
        gui.setItem(16, level);

        ItemStack backBtn = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backBtn.getItemMeta();
        backMeta.setDisplayName("§c← Back to Area Edit");
        backBtn.setItemMeta(backMeta);
        gui.setItem(31, backBtn);

        player.openInventory(gui);
    }
}