package com.xmobgeneration.models;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class MobStats implements ConfigurationSerializable {
    private String mobName;
    private double health;
    private double damage;
    private int level;
    private boolean showBossBar;
    private String bossBarColor;
    private String bossBarStyle;
    private boolean bossBarVisible;
    private int bossBarRange;
    private Map<String, PotionEffectData> potionEffects;

    public static class PotionEffectData implements ConfigurationSerializable {
        private String type;
        private int amplifier;
        private int duration;
        private boolean ambient;
        private boolean particles;

        public PotionEffectData(String type, int amplifier, int duration, boolean ambient, boolean particles) {
            this.type = type;
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.particles = particles;
        }

        public PotionEffectData(Map<String, Object> map) {
            this.type = (String) map.getOrDefault("type", "SPEED");
            this.amplifier = (int) map.getOrDefault("amplifier", 0);
            this.duration = (int) map.getOrDefault("duration", -1);
            this.ambient = (boolean) map.getOrDefault("ambient", true);
            this.particles = (boolean) map.getOrDefault("particles", false);
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("type", type);
            map.put("amplifier", amplifier);
            map.put("duration", duration);
            map.put("ambient", ambient);
            map.put("particles", particles);
            return map;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getAmplifier() { return amplifier; }
        public void setAmplifier(int amplifier) { this.amplifier = Math.max(0, Math.min(255, amplifier)); }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public boolean isAmbient() { return ambient; }
        public void setAmbient(boolean ambient) { this.ambient = ambient; }
        public boolean hasParticles() { return particles; }
        public void setParticles(boolean particles) { this.particles = particles; }

        public PotionEffectType getPotionEffectType() {
            try {
                return PotionEffectType.getByName(type);
            } catch (Exception e) {
                return PotionEffectType.SPEED;
            }
        }
    }

    public MobStats() {
        this.mobName = "Monster";
        this.health = 20.0;
        this.damage = 2.0;
        this.level = 1;
        this.showBossBar = true;
        this.bossBarColor = "RED";
        this.bossBarStyle = "SOLID";
        this.bossBarVisible = true;
        this.bossBarRange = 32;
        this.potionEffects = new HashMap<>();
    }

    public MobStats(Map<String, Object> map) {
        this.mobName = (String) map.getOrDefault("mobName", "Monster");
        this.health = ((Number) map.getOrDefault("health", 20.0)).doubleValue();
        this.damage = ((Number) map.getOrDefault("damage", 2.0)).doubleValue();
        this.level = (int) map.getOrDefault("level", 1);
        this.showBossBar = (boolean) map.getOrDefault("showBossBar", 
            map.getOrDefault("showName", true));
        this.bossBarColor = (String) map.getOrDefault("bossBarColor", "RED");
        this.bossBarStyle = (String) map.getOrDefault("bossBarStyle", "SOLID");
        this.bossBarVisible = (boolean) map.getOrDefault("bossBarVisible", true);
        this.bossBarRange = (int) map.getOrDefault("bossBarRange", 32);
        this.potionEffects = new HashMap<>();
        
        Object effectsObj = map.get("potionEffects");
        if (effectsObj instanceof Map) {
            Map<String, Object> effectsMap = (Map<String, Object>) effectsObj;
            for (Map.Entry<String, Object> entry : effectsMap.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    potionEffects.put(entry.getKey(), new PotionEffectData((Map<String, Object>) entry.getValue()));
                }
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("mobName", mobName);
        map.put("health", health);
        map.put("damage", damage);
        map.put("level", level);
        map.put("showBossBar", showBossBar);
        map.put("bossBarColor", bossBarColor);
        map.put("bossBarStyle", bossBarStyle);
        map.put("bossBarVisible", bossBarVisible);
        map.put("bossBarRange", bossBarRange);
        
        Map<String, Object> effectsMap = new HashMap<>();
        for (Map.Entry<String, PotionEffectData> entry : potionEffects.entrySet()) {
            effectsMap.put(entry.getKey(), entry.getValue().serialize());
        }
        map.put("potionEffects", effectsMap);
        
        return map;
    }

    public String getMobName() { return mobName; }
    public void setMobName(String mobName) { this.mobName = mobName; }

    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = Math.max(1.0, health); }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = Math.max(0.0, damage); }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }

    public boolean isShowBossBar() { return showBossBar; }
    public void setShowBossBar(boolean showBossBar) { this.showBossBar = showBossBar; }
    
    @Deprecated
    public boolean isShowName() { return showBossBar; }
    @Deprecated
    public void setShowName(boolean showName) { this.showBossBar = showName; }

    public String getBossBarColor() { return bossBarColor; }
    public void setBossBarColor(String bossBarColor) { this.bossBarColor = bossBarColor; }

    public String getBossBarStyle() { return bossBarStyle; }
    public void setBossBarStyle(String bossBarStyle) { this.bossBarStyle = bossBarStyle; }

    public boolean isBossBarVisible() { return bossBarVisible; }
    public void setBossBarVisible(boolean bossBarVisible) { this.bossBarVisible = bossBarVisible; }

    public int getBossBarRange() { return bossBarRange; }
    public void setBossBarRange(int bossBarRange) { this.bossBarRange = Math.max(8, Math.min(128, bossBarRange)); }

    public Map<String, PotionEffectData> getPotionEffects() { return potionEffects; }

    public void addPotionEffect(String id, PotionEffectData effect) {
        potionEffects.put(id, effect);
    }

    public void removePotionEffect(String id) {
        potionEffects.remove(id);
    }

    public BarColor getBarColor() {
        try {
            return BarColor.valueOf(bossBarColor);
        } catch (IllegalArgumentException e) {
            return BarColor.RED;
        }
    }

    public BarStyle getBarStyle() {
        try {
            return BarStyle.valueOf(bossBarStyle);
        } catch (IllegalArgumentException e) {
            return BarStyle.SOLID;
        }
    }

    public String getDisplayName() {
        return String.format("§7[Lv.%d] §f%s", level, mobName);
    }
}