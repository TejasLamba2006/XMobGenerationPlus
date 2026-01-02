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
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CustomDropsMenu {
    private final XMobGeneration plugin;
    private static final int DROPS_INVENTORY_SIZE = 54;
    private static final int TOGGLE_BUTTON_SLOT = 49;
    private static final int SAVE_BUTTON_SLOT = 53;
    private static final int BACK_BUTTON_SLOT = 45;

    private final Map<String, Map<Integer, Double>> areaItemChances = new HashMap<>();

    public CustomDropsMenu(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, SpawnArea area) {
        Inventory gui = Bukkit.createInventory(null, DROPS_INVENTORY_SIZE,
                "§8Custom Drops - " + area.getName());

        Map<Integer, Double> itemChances = areaItemChances.computeIfAbsent(
                area.getName(),
                k -> new HashMap<>());

        populateInventory(gui, area, itemChances);
        player.openInventory(gui);
    }

    private void populateInventory(Inventory gui, SpawnArea area, Map<Integer, Double> itemChances) {
        int slot = 0;
        List<ItemStack> items = area.getCustomDrops().getItems();
        List<Double> chances = area.getCustomDrops().getChances();

        for (int i = 0; i < items.size(); i++) {
            if (slot < 45) {
                ItemStack item = items.get(i).clone();
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                    lore.removeIf(line -> line.contains("Drop Chance:") || line.contains("Edit chance")
                            || line.contains("Right-Click"));

                    double chance = chances.get(i);
                    itemChances.put(slot, chance);

                    lore.add("");
                    lore.add("§7Drop Chance: §e" + chance + "%");
                    lore.add("§7Right-Click: §c-10% | §7Shift+Right: §a+10%");

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                gui.setItem(slot++, item);
            }
        }

        gui.setItem(TOGGLE_BUTTON_SLOT, createToggleButton(area.getCustomDrops().isEnabled()));
        gui.setItem(SAVE_BUTTON_SLOT, createSaveButton());
        gui.setItem(BACK_BUTTON_SLOT, createBackButton());

        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§eHow to Use");
        infoMeta.setLore(Arrays.asList(
                "§7Drag items into the top slots",
                "§7to add them as drops",
                "",
                "§7Right-Click item: §c-10% chance",
                "§7Shift+Right-Click: §a+10% chance",
                "",
                "§7Items save automatically"));
        infoItem.setItemMeta(infoMeta);
        gui.setItem(47, infoItem);
    }

    private ItemStack createToggleButton(boolean enabled) {
        ItemStack button = new ItemStack(enabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(enabled ? "§aCustom Drops Enabled" : "§cCustom Drops Disabled");
            meta.setLore(Arrays.asList("§7Click to toggle custom drops"));
            button.setItemMeta(meta);
        }
        return button;
    }

    private ItemStack createSaveButton() {
        ItemStack button = new ItemStack(Material.EMERALD);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aSave & Close");
            meta.setLore(Arrays.asList(
                    "§7Click to save and return"));
            button.setItemMeta(meta);
        }
        return button;
    }

    private ItemStack createBackButton() {
        ItemStack button = new ItemStack(Material.ARROW);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c← Back to Area Edit");
            meta.setLore(Arrays.asList("§7Changes are auto-saved"));
            button.setItemMeta(meta);
        }
        return button;
    }

    public void handleInventoryClose(InventoryCloseEvent event, SpawnArea area) {
        List<ItemStack> items = new ArrayList<>();
        List<Double> chances = new ArrayList<>();
        Map<Integer, Double> itemChances = areaItemChances.getOrDefault(area.getName(), new HashMap<>());

        for (int i = 0; i < 45; i++) {
            ItemStack item = event.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.add(cleanItem(item.clone()));
                chances.add(itemChances.getOrDefault(i, 100.0));
            }
        }

        area.getCustomDrops().setItems(items, chances);
        plugin.getAreaManager().saveAreas();
        areaItemChances.remove(area.getName());
    }

    private ItemStack cleanItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.removeIf(line -> line.contains("Drop Chance:") || line.contains("Edit chance")
                    || line.contains("Right-Click"));
            meta.setLore(lore.isEmpty() ? null : lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void saveCustomDrops(Player player, SpawnArea area) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        List<ItemStack> items = new ArrayList<>();
        List<Double> chances = new ArrayList<>();
        Map<Integer, Double> itemChances = areaItemChances.getOrDefault(area.getName(), new HashMap<>());

        for (int i = 0; i < 45; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.add(cleanItem(item.clone()));
                chances.add(itemChances.getOrDefault(i, 100.0));
            }
        }

        area.getCustomDrops().setItems(items, chances);
        plugin.getAreaManager().saveAreas();
        areaItemChances.remove(area.getName());
    }

    public void adjustChance(String areaName, int slot, double adjustment) {
        Map<Integer, Double> chances = areaItemChances.computeIfAbsent(areaName, k -> new HashMap<>());
        double current = chances.getOrDefault(slot, 100.0);
        double newChance = Math.max(0, Math.min(100, current + adjustment));
        chances.put(slot, newChance);
    }

    public double getChance(String areaName, int slot) {
        return areaItemChances.getOrDefault(areaName, new HashMap<>()).getOrDefault(slot, 100.0);
    }
}