package com.thenexusreborn.survivalgames.lootv2;

import org.bukkit.Material;

import java.util.*;
import java.util.Map.Entry;

public class LootChances {
    private final String[] categoryChances;
    private final Map<String, Material[]> materialChances = new HashMap<>();
    
    public LootChances(List<String> categoryChances, Map<String, List<Material>> entryChances) {
        this.categoryChances = categoryChances.toArray(new String[0]);
        for (Entry<String, List<Material>> entry : entryChances.entrySet()) {
            this.materialChances.put(entry.getKey(), entry.getValue().toArray(new Material[0]));
        }
    }
    
    public String[] getCategoryChances() {
        return categoryChances;
    }
    
    public Map<String, Material[]> getMaterialChances() {
        return materialChances;
    }
    
    public Material[] getMaterialChances(String category) {
        return materialChances.get(category);
    }
    
    @Override
    public String toString() {
        return "LootChances{" +
                "categoryChances=" + Arrays.toString(categoryChances) +
                ", materialChances=" + materialChances +
                '}';
    }
}
