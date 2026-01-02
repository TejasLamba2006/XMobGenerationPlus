package com.xmobgeneration.listeners;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.gui.menus.MobTypeMenu;
import com.xmobgeneration.models.SpawnArea;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIListener implements Listener {
    private final XMobGeneration plugin;

    public GUIListener(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("§8"))
            return;

        if (title.startsWith("§8Custom Drops - ")) {
            for (int slot : event.getRawSlots()) {
                if (slot >= 45) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§8"))
            return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (title.equals("§8XMobGenerationPlus")) {
            event.setCancelled(true);
            if (clicked != null)
                handleMainMenu(player, clicked);
        } else if (title.startsWith("§8Areas - ")) {
            event.setCancelled(true);
            if (clicked != null)
                handleAreaList(player, clicked, title);
        } else if (title.startsWith("§8Edit Area - ")) {
            event.setCancelled(true);
            if (clicked != null)
                handleAreaEdit(player, clicked, title, event);
        } else if (title.startsWith("§8Custom Drops - ")) {
            handleCustomDrops(event, title, player);
        } else if (title.startsWith("§8Mob Equipment - ")) {
            handleMobEquipment(event);
        } else if (title.startsWith("§8Mob Stats - ")) {
            event.setCancelled(true);
            if (clicked != null)
                handleMobStats(player, clicked, title, event);
        } else if (title.startsWith("§8Select Mob Type")) {
            event.setCancelled(true);
            if (clicked != null)
                handleMobTypeSelection(player, clicked, title);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith("§8Custom Drops - ")) {
            String areaName = title.substring("§8Custom Drops - ".length());
            SpawnArea area = plugin.getAreaManager().getArea(areaName);
            if (area != null) {
                plugin.getGUIManager().getCustomDropsMenu().handleInventoryClose(event, area);
            }
        }
    }

    private void handleMobTypeSelection(Player player, ItemStack clicked, String title) {
        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            int currentPage = MobTypeMenu.getCurrentPage(title);
            String areaName = plugin.getGUIManager().getMobTypeMenu().getSelectedArea(player);
            SpawnArea area = plugin.getAreaManager().getArea(areaName);

            if (area == null)
                return;

            if (displayName.contains("Previous")) {
                plugin.getGUIManager().openMobTypeMenu(player, area, currentPage - 1);
            } else if (displayName.contains("Next")) {
                plugin.getGUIManager().openMobTypeMenu(player, area, currentPage + 1);
            }
        } else if (clicked.getType() == Material.BARRIER) {
            String areaName = plugin.getGUIManager().getMobTypeMenu().getSelectedArea(player);
            SpawnArea area = plugin.getAreaManager().getArea(areaName);
            if (area != null) {
                plugin.getGUIManager().getMobTypeMenu().clearSelection(player);
                plugin.getGUIManager().openAreaEditGUI(player, area);
            }
        } else if (clicked.getType() == Material.PAPER) {
            return;
        } else if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String areaName = plugin.getGUIManager().getMobTypeMenu().getSelectedArea(player);
            SpawnArea area = plugin.getAreaManager().getArea(areaName);

            if (area == null)
                return;

            EntityType mobType = MobTypeMenu.getEntityTypeFromDisplayName(clicked.getItemMeta().getDisplayName());
            if (mobType != null) {
                area.setMobType(mobType);
                area.setMythicMob(false);
                area.setMythicMobType("");
                plugin.getAreaManager().saveAreas();
                plugin.getSpawnManager().restartArea(area);
                player.sendMessage("§aMob type set to " + mobType.name());
                plugin.getGUIManager().getMobTypeMenu().clearSelection(player);
                plugin.getGUIManager().openAreaEditGUI(player, area);
            }
        }
    }

    private void handleMobStats(Player player, ItemStack clicked, String title, InventoryClickEvent event) {
        String areaName = title.substring("§8Mob Stats - ".length());
        SpawnArea area = plugin.getAreaManager().getArea(areaName);

        if (area == null)
            return;

        Material type = clicked.getType();
        boolean shift = event.isShiftClick();
        boolean left = event.isLeftClick();
        boolean right = event.isRightClick();

        if (type == Material.ARROW) {
            plugin.getGUIManager().openAreaEditGUI(player, area);
            return;
        }

        if (type == Material.NAME_TAG) {
            if (shift) {
                player.closeInventory();
                player.sendMessage("§aType the new mob name in chat. Type 'cancel' to cancel.");
                plugin.getServer().getPluginManager().registerEvents(
                        new MobNameInputListener(plugin, player, area), plugin);
            } else {
                area.getMobStats().setShowName(!area.getMobStats().isShowName());
            }
        } else if (type == Material.RED_DYE) {
            double change = shift ? (left ? 50 : -50) : (left ? 5 : -5);
            area.getMobStats().setHealth(Math.max(1, area.getMobStats().getHealth() + change));
        } else if (type == Material.IRON_SWORD) {
            double change = shift ? (left ? 5 : -5) : (left ? 1 : -1);
            area.getMobStats().setDamage(Math.max(0, area.getMobStats().getDamage() + change));
        } else if (type == Material.EXPERIENCE_BOTTLE) {
            int change = shift ? (left ? 10 : -10) : (left ? 1 : -1);
            area.getMobStats().setLevel(Math.max(1, area.getMobStats().getLevel() + change));
        }

        plugin.getAreaManager().saveAreas();
        plugin.getGUIManager().openMobStatsMenu(player, area);
    }

    private void handleMobEquipment(InventoryClickEvent event) {
        String areaName = event.getView().getTitle().substring("§8Mob Equipment - ".length());
        SpawnArea area = plugin.getAreaManager().getArea(areaName);

        if (area == null)
            return;

        int slot = event.getRawSlot();
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (slot == 8) {
            event.setCancelled(true);
            plugin.getGUIManager().openAreaEditGUI((Player) event.getWhoClicked(), area);
            return;
        }

        if (slot >= 0 && slot <= 4) {
            event.setCancelled(true);

            if (cursor != null && cursor.getType() != Material.AIR) {
                boolean validItem = false;
                switch (slot) {
                    case 0:
                        validItem = isHelmet(cursor.getType());
                        if (validItem)
                            area.getMobEquipment().setHelmet(cursor.clone());
                        break;
                    case 1:
                        validItem = isChestplate(cursor.getType());
                        if (validItem)
                            area.getMobEquipment().setChestplate(cursor.clone());
                        break;
                    case 2:
                        validItem = isLeggings(cursor.getType());
                        if (validItem)
                            area.getMobEquipment().setLeggings(cursor.clone());
                        break;
                    case 3:
                        validItem = isBoots(cursor.getType());
                        if (validItem)
                            area.getMobEquipment().setBoots(cursor.clone());
                        break;
                    case 4:
                        area.getMobEquipment().setOffHand(cursor.clone());
                        validItem = true;
                        break;
                }

                if (validItem) {
                    plugin.getAreaManager().saveAreas();
                    plugin.getGUIManager().openCustomMobEquipmentMenu((Player) event.getWhoClicked(), area);
                }
            } else if (clicked != null && clicked.getType() != Material.ARMOR_STAND) {
                switch (slot) {
                    case 0:
                        area.getMobEquipment().setHelmet(null);
                        break;
                    case 1:
                        area.getMobEquipment().setChestplate(null);
                        break;
                    case 2:
                        area.getMobEquipment().setLeggings(null);
                        break;
                    case 3:
                        area.getMobEquipment().setBoots(null);
                        break;
                    case 4:
                        area.getMobEquipment().setOffHand(null);
                        break;
                }
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openCustomMobEquipmentMenu((Player) event.getWhoClicked(), area);
            }
        }
    }

    private void handleCustomDrops(InventoryClickEvent event, String title, Player player) {
        String areaName = title.substring("§8Custom Drops - ".length());
        SpawnArea area = plugin.getAreaManager().getArea(areaName);

        if (area == null)
            return;

        int slot = event.getRawSlot();

        if (slot < 45) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() != Material.AIR) {
                if (event.isRightClick()) {
                    event.setCancelled(true);
                    double adjustment = event.isShiftClick() ? 10 : -10;
                    plugin.getGUIManager().getCustomDropsMenu().adjustChance(areaName, slot, adjustment);

                    double newChance = plugin.getGUIManager().getCustomDropsMenu().getChance(areaName, slot);
                    ItemMeta meta = clicked.getItemMeta();
                    if (meta != null) {
                        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                        lore.removeIf(line -> line.contains("Drop Chance:") || line.contains("Right-Click"));
                        lore.add("");
                        lore.add("§7Drop Chance: §e" + newChance + "%");
                        lore.add("§7Right-Click: §c-10% | §7Shift+Right: §a+10%");
                        meta.setLore(lore);
                        clicked.setItemMeta(meta);
                    }
                }
            }
            return;
        }

        if (slot >= 45 && slot < event.getInventory().getSize()) {
            event.setCancelled(true);

            if (slot == 49 && event.getCurrentItem() != null) {
                area.getCustomDrops().setEnabled(!area.getCustomDrops().isEnabled());
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openCustomDropsMenu(player, area);
            } else if (slot == 53) {
                plugin.getGUIManager().getCustomDropsMenu().saveCustomDrops(player, area);
                plugin.getGUIManager().openAreaEditGUI(player, area);
            } else if (slot == 45) {
                plugin.getGUIManager().getCustomDropsMenu().saveCustomDrops(player, area);
                plugin.getGUIManager().openAreaEditGUI(player, area);
            }
        }
    }

    private void handleMainMenu(Player player, ItemStack clicked) {
        switch (clicked.getType()) {
            case EMERALD_BLOCK:
                player.closeInventory();
                player.sendMessage("§aUse WorldEdit to select an area, then use §e/xmg create <name>");
                break;
            case ANVIL:
                plugin.getGUIManager().openAreaListGUI(player, "edit");
                break;
            case REDSTONE_BLOCK:
                plugin.getGUIManager().openAreaListGUI(player, "delete");
                break;
        }
    }

    private void handleAreaList(Player player, ItemStack clicked, String title) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openMainGUI(player);
            return;
        }

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName())
            return;

        String displayName = clicked.getItemMeta().getDisplayName();
        if (!displayName.startsWith("§e"))
            return;

        String areaName = displayName.substring(2);
        SpawnArea area = plugin.getAreaManager().getArea(areaName);

        if (area == null)
            return;

        if (title.endsWith("edit")) {
            plugin.getGUIManager().openAreaEditGUI(player, area);
        } else if (title.endsWith("delete")) {
            plugin.getAreaManager().deleteArea(areaName);
            player.sendMessage(plugin.getConfigManager().getMessage("area-deleted")
                    .replace("%name%", areaName));
            plugin.getGUIManager().openAreaListGUI(player, "delete");
        } else if (title.endsWith("list")) {
            boolean enabled = plugin.getSpawnManager().toggleSpawning(area);
            player.sendMessage("§7Spawning for area §e" + areaName + " §7is now " +
                    (enabled ? "§aenabled" : "§cdisabled"));
            plugin.getAreaManager().saveAreas();
            plugin.getGUIManager().openAreaListGUI(player, "list");
        }
    }

    private void handleAreaEdit(Player player, ItemStack clicked, String title, InventoryClickEvent event) {
        String areaName = title.substring("§8Edit Area - ".length());
        SpawnArea area = plugin.getAreaManager().getArea(areaName);

        if (area == null)
            return;

        Material type = clicked.getType();
        boolean shift = event.isShiftClick();
        boolean left = event.isLeftClick();

        switch (type) {
            case ZOMBIE_SPAWN_EGG:
                plugin.getGUIManager().openMobTypeMenu(player, area, 0);
                break;
            case REPEATER:
                int spawnChange = shift ? (left ? 10 : -10) : (left ? 1 : -1);
                area.setSpawnCount(Math.max(1, area.getSpawnCount() + spawnChange));
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case CLOCK:
                int delayChange = shift ? (left ? 30 : -30) : (left ? 5 : -5);
                area.setRespawnDelay(Math.max(1, area.getRespawnDelay() + delayChange));
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case ENDER_EYE:
            case ENDER_PEARL:
                if (left && !shift) {
                    area.setPlayerProximityRequired(!area.isPlayerProximityRequired());
                } else if (event.isRightClick()) {
                    int rangeChange = shift ? -10 : 10;
                    area.setProximityRange(Math.max(10, area.getProximityRange() + rangeChange));
                }
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case EXPERIENCE_BOTTLE:
                int xpChange = shift ? (left ? 50 : -50) : (left ? 10 : -10);
                area.setXpAmount(Math.max(0, area.getXpAmount() + xpChange));
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case ENCHANTED_BOOK:
                if (shift) {
                    int maxChange = left ? 1 : -1;
                    area.setMaxLevel(area.getMaxLevel() + maxChange);
                } else {
                    int minChange = left ? 1 : -1;
                    area.setMinLevel(area.getMinLevel() + minChange);
                }
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case DIAMOND_CHESTPLATE:
                plugin.getGUIManager().openCustomMobEquipmentMenu(player, area);
                break;
            case CHEST:
                plugin.getGUIManager().openCustomDropsMenu(player, area);
                break;
            case CRAFTING_TABLE:
                plugin.getGUIManager().openMobStatsMenu(player, area);
                break;
            case DRAGON_HEAD:
            case SKELETON_SKULL:
                area.setBossArea(!area.isBossArea());
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case LIME_DYE:
            case GRAY_DYE:
                boolean enabled = plugin.getSpawnManager().toggleSpawning(area);
                player.sendMessage("§7Spawning for area §e" + areaName + " §7is now " +
                        (enabled ? "§aenabled" : "§cdisabled"));
                plugin.getAreaManager().saveAreas();
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
            case ARROW:
                plugin.getGUIManager().openAreaListGUI(player, "edit");
                break;
            case REDSTONE:
                plugin.getSpawnManager().restartArea(area);
                player.sendMessage("§aArea " + areaName + " has been restarted!");
                plugin.getGUIManager().openAreaEditGUI(player, area);
                break;
        }
    }

    private boolean isHelmet(Material material) {
        return material.name().endsWith("_HELMET") || material == Material.CARVED_PUMPKIN
                || material == Material.PLAYER_HEAD || material == Material.TURTLE_HELMET;
    }

    private boolean isChestplate(Material material) {
        return material.name().endsWith("_CHESTPLATE") || material == Material.ELYTRA;
    }

    private boolean isLeggings(Material material) {
        return material.name().endsWith("_LEGGINGS");
    }

    private boolean isBoots(Material material) {
        return material.name().endsWith("_BOOTS");
    }
}