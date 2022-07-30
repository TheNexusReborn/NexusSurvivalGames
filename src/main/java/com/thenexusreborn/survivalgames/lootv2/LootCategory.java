package com.thenexusreborn.survivalgames.lootv2;

import org.bukkit.Material;

import java.util.*;

public class LootCategory {
    private final String name;
    private final Rarity rarity;
    private final List<LootEntry> entries;
    
    public LootCategory(String name, Rarity rarity, List<LootEntry> entries) {
        this.name = name;
        this.rarity = rarity;
        this.entries = entries;
    }
    
    public LootCategory(String name, Rarity rarity) {
        this(name, rarity, new ArrayList<>());
    }
    
    public LootEntry getEntry(String name) {
        for (LootEntry entry : this.entries) {
            if (entry.getName().equalsIgnoreCase(name)) {
                return entry;
            }
        }
        
        return null;
    }
    
    public LootEntry getEntry(Material material) {
        for (LootEntry entry : this.entries) {
            if (entry.getMaterial() == material) {
                return entry;
            }
        }
        
        return null;
    }
    
    public void addEntry(LootEntry lootEntry) {
        this.entries.add(lootEntry);
    }
    
    public String getName() {
        return name;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public List<LootEntry> getEntries() {
        return entries;
    }
}
