package com.xmobgeneration.gui.menus;

import com.xmobgeneration.XMobGeneration;
import com.xmobgeneration.models.SpawnArea;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MobTypeMenu {
    private final XMobGeneration plugin;
    private static final int ITEMS_PER_PAGE = 45;

    private static final List<EntityType> HOSTILE_MOBS = Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.WITCH,
            EntityType.BLAZE,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.GHAST,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.HOGLIN,
            EntityType.ZOGLIN,
            EntityType.WITHER_SKELETON,
            EntityType.STRAY,
            EntityType.HUSK,
            EntityType.DROWNED,
            EntityType.PHANTOM,
            EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN,
            EntityType.SHULKER,
            EntityType.ENDERMITE,
            EntityType.SILVERFISH,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.EVOKER,
            EntityType.WARDEN,
            EntityType.BREEZE,
            EntityType.BOGGED);

    private static final Map<EntityType, Material> MOB_EGGS = new HashMap<>();

    static {
        MOB_EGGS.put(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG);
        MOB_EGGS.put(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.WITCH, Material.WITCH_SPAWN_EGG);
        MOB_EGGS.put(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.SLIME, Material.SLIME_SPAWN_EGG);
        MOB_EGGS.put(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.GHAST, Material.GHAST_SPAWN_EGG);
        MOB_EGGS.put(EntityType.ZOMBIFIED_PIGLIN, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.PIGLIN, Material.PIGLIN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.PIGLIN_BRUTE, Material.PIGLIN_BRUTE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.HOGLIN, Material.HOGLIN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.ZOGLIN, Material.ZOGLIN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG);
        MOB_EGGS.put(EntityType.STRAY, Material.STRAY_SPAWN_EGG);
        MOB_EGGS.put(EntityType.HUSK, Material.HUSK_SPAWN_EGG);
        MOB_EGGS.put(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG);
        MOB_EGGS.put(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG);
        MOB_EGGS.put(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.SHULKER, Material.SHULKER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.ENDERMITE, Material.ENDERMITE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG);
        MOB_EGGS.put(EntityType.VEX, Material.VEX_SPAWN_EGG);
        MOB_EGGS.put(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG);
        MOB_EGGS.put(EntityType.PILLAGER, Material.PILLAGER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.RAVAGER, Material.RAVAGER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG);
        MOB_EGGS.put(EntityType.WARDEN, Material.WARDEN_SPAWN_EGG);
        MOB_EGGS.put(EntityType.BREEZE, Material.BREEZE_SPAWN_EGG);
        MOB_EGGS.put(EntityType.BOGGED, Material.BOGGED_SPAWN_EGG);
    }

    private final Map<UUID, String> playerAreaSelection = new HashMap<>();

    public MobTypeMenu(XMobGeneration plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, SpawnArea area, int page) {
        playerAreaSelection.put(player.getUniqueId(), area.getName());

        int totalPages = (int) Math.ceil((double) HOSTILE_MOBS.size() / ITEMS_PER_PAGE);
        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, 54, "§8Select Mob Type - Page " + (page + 1));

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, HOSTILE_MOBS.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            EntityType type = HOSTILE_MOBS.get(i);
            ItemStack item = new ItemStack(getMobEgg(type));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + formatMobName(type));
            List<String> lore = new ArrayList<>();
            if (type == area.getMobType() && !area.isMythicMob()) {
                lore.add("§aCurrent selection");
            }
            lore.add("§7Click to select");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(slot++, item);
        }

        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§a← Previous Page");
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        ItemStack backBtn = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backBtn.getItemMeta();
        backMeta.setDisplayName("§c← Back to Area Edit");
        backBtn.setItemMeta(backMeta);
        gui.setItem(49, backBtn);

        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§aNext Page →");
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName("§7Page " + (page + 1) + "/" + totalPages);
        pageInfo.setItemMeta(pageMeta);
        gui.setItem(52, pageInfo);

        player.openInventory(gui);
    }

    public String getSelectedArea(Player player) {
        return playerAreaSelection.get(player.getUniqueId());
    }

    public void clearSelection(Player player) {
        playerAreaSelection.remove(player.getUniqueId());
    }

    public static EntityType getEntityTypeFromDisplayName(String displayName) {
        String cleanName = displayName.substring(2);
        for (EntityType type : HOSTILE_MOBS) {
            if (formatMobNameStatic(type).equals(cleanName)) {
                return type;
            }
        }
        return null;
    }

    public static int getCurrentPage(String title) {
        try {
            String pageStr = title.substring(title.lastIndexOf("Page ") + 5);
            return Integer.parseInt(pageStr) - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private Material getMobEgg(EntityType type) {
        return MOB_EGGS.getOrDefault(type, Material.ZOMBIE_SPAWN_EGG);
    }

    private String formatMobName(EntityType type) {
        return formatMobNameStatic(type);
    }

    private static String formatMobNameStatic(EntityType type) {
        String name = type.name().toLowerCase().replace("_", " ");
        StringBuilder result = new StringBuilder();
        for (String word : name.split(" ")) {
            if (result.length() > 0)
                result.append(" ");
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    public static List<EntityType> getHostileMobs() {
        return Collections.unmodifiableList(HOSTILE_MOBS);
    }
}