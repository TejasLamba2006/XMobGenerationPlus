package com.xmobgeneration.models;

import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class CustomDrops implements ConfigurationSerializable {
    public enum DropLocation { LAST_MOB, SPAWN_CENTER, EACH_MOB }
    
    private final List<ItemStack> items;
    private final List<Double> chances;
    private boolean enabled;
    private DropLocation dropLocation;
    private boolean beaconEnabled;
    private String beaconColor;
    private int beaconDuration;
    private boolean soundEnabled;
    private String soundType;
    private float soundVolume;
    private float soundPitch;
    private int notifyRadius;

    public CustomDrops() {
        this.items = new ArrayList<>();
        this.chances = new ArrayList<>();
        this.enabled = false;
        this.dropLocation = DropLocation.EACH_MOB;
        this.beaconEnabled = true;
        this.beaconColor = "GOLD";
        this.beaconDuration = 100;
        this.soundEnabled = true;
        this.soundType = "ENTITY_PLAYER_LEVELUP";
        this.soundVolume = 1.0f;
        this.soundPitch = 1.0f;
        this.notifyRadius = 64;
    }

    public CustomDrops(Map<String, Object> map) {
        this.items = (List<ItemStack>) map.getOrDefault("items", new ArrayList<>());
        this.chances = (List<Double>) map.getOrDefault("chances", new ArrayList<>());
        this.enabled = (boolean) map.getOrDefault("enabled", false);
        String locStr = (String) map.getOrDefault("dropLocation", "EACH_MOB");
        this.dropLocation = DropLocation.valueOf(locStr);
        this.beaconEnabled = (boolean) map.getOrDefault("beaconEnabled", true);
        this.beaconColor = (String) map.getOrDefault("beaconColor", "GOLD");
        this.beaconDuration = (int) map.getOrDefault("beaconDuration", 100);
        this.soundEnabled = (boolean) map.getOrDefault("soundEnabled", true);
        this.soundType = (String) map.getOrDefault("soundType", "ENTITY_PLAYER_LEVELUP");
        this.soundVolume = ((Number) map.getOrDefault("soundVolume", 1.0f)).floatValue();
        this.soundPitch = ((Number) map.getOrDefault("soundPitch", 1.0f)).floatValue();
        this.notifyRadius = (int) map.getOrDefault("notifyRadius", 64);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("items", items);
        map.put("chances", chances);
        map.put("enabled", enabled);
        map.put("dropLocation", dropLocation.name());
        map.put("beaconEnabled", beaconEnabled);
        map.put("beaconColor", beaconColor);
        map.put("beaconDuration", beaconDuration);
        map.put("soundEnabled", soundEnabled);
        map.put("soundType", soundType);
        map.put("soundVolume", soundVolume);
        map.put("soundPitch", soundPitch);
        map.put("notifyRadius", notifyRadius);
        return map;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    public List<Double> getChances() {
        return new ArrayList<>(chances);
    }

    public void setItems(List<ItemStack> items, List<Double> chances) {
        this.items.clear();
        this.chances.clear();
        this.items.addAll(items);
        this.chances.addAll(chances);
    }

    public void addItem(ItemStack item, double chance) {
        items.add(item.clone());
        chances.add(Math.min(100.0, Math.max(0.0, chance)));
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            chances.remove(index);
        }
    }

    public double getChance(int index) {
        return index >= 0 && index < chances.size() ? chances.get(index) : 0.0;
    }

    public void setChance(int index, double chance) {
        if (index >= 0 && index < chances.size()) {
            chances.set(index, Math.min(100.0, Math.max(0.0, chance)));
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public DropLocation getDropLocation() { return dropLocation; }
    public void setDropLocation(DropLocation dropLocation) { this.dropLocation = dropLocation; }
    
    public boolean isBeaconEnabled() { return beaconEnabled; }
    public void setBeaconEnabled(boolean beaconEnabled) { this.beaconEnabled = beaconEnabled; }
    
    public String getBeaconColor() { return beaconColor; }
    public void setBeaconColor(String beaconColor) { this.beaconColor = beaconColor; }
    
    public int getBeaconDuration() { return beaconDuration; }
    public void setBeaconDuration(int beaconDuration) { this.beaconDuration = Math.max(20, beaconDuration); }
    
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }
    
    public String getSoundType() { return soundType; }
    public void setSoundType(String soundType) { this.soundType = soundType; }
    
    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float soundVolume) { this.soundVolume = Math.max(0.1f, Math.min(2.0f, soundVolume)); }
    
    public float getSoundPitch() { return soundPitch; }
    public void setSoundPitch(float soundPitch) { this.soundPitch = Math.max(0.5f, Math.min(2.0f, soundPitch)); }
    
    public int getNotifyRadius() { return notifyRadius; }
    public void setNotifyRadius(int notifyRadius) { this.notifyRadius = Math.max(16, notifyRadius); }

    public Sound getSound() {
        try {
            return Sound.valueOf(soundType);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }

    public Color getBukkitBeaconColor() {
        switch (beaconColor.toUpperCase()) {
            case "RED": return Color.RED;
            case "BLUE": return Color.BLUE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "PURPLE": return Color.PURPLE;
            case "WHITE": return Color.WHITE;
            case "AQUA": return Color.AQUA;
            case "ORANGE": return Color.ORANGE;
            case "GOLD": default: return Color.fromRGB(255, 215, 0);
        }
    }
}