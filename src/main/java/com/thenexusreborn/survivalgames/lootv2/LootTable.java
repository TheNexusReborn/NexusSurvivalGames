package com.thenexusreborn.survivalgames.lootv2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    private final String name;
    private final List<LootCategory> categories;
    
    public LootTable(String name, List<LootCategory> categories) {
        this.name = name;
        this.categories = categories;
    }
    
    public List<ItemStack> generateLoot(int maxAmount, LootChances lootChances) {
        long start = System.currentTimeMillis();
        List<ItemStack> loot = new ArrayList<>();
        
        for (int i = 0; i < maxAmount; i++) {
            String[] categoryChances = lootChances.getCategoryChances();
            LootCategory category = getCategory(categoryChances[new Random().nextInt(categoryChances.length)]);
            Material[] materialChances = lootChances.getMaterialChances(category.getName());
            Material entryChoice = materialChances[new Random().nextInt(materialChances.length)];
            LootEntry entry = category.getEntry(entryChoice);
            if (entry != null) {
                loot.add(entry.generateItemStack());
            } else {
                System.out.println("Had a null choice...");
            }
        }
        
        long end = System.currentTimeMillis();
        
        long time = end - start;
        if (time > 1) {
            System.out.println("Loot generation took " + time + "ms.");
        }
        
        return loot;
    }
    
    public void addCategory(LootCategory category) {
        this.categories.add(category);
    }
    
    public LootCategory getCategory(String name) {
        for (LootCategory category : this.categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        
        return null;
    }
    
    public LootTable(String name) {
        this(name, new ArrayList<>());
    }
    
    public String getName() {
        return name;
    }
    
    public List<LootCategory> getCategories() {
        return categories;
    }
}
