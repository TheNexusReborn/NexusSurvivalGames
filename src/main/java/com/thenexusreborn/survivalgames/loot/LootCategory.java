package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.api.util.Range;
import org.bukkit.Material;

import java.util.*;

public class LootCategory {
    private final String name;
    private final Rarity rarity;
    private final List<LootEntry> entries;
    private final int maxAmountPerChest;
    
    private Set<Range<LootEntry>> entryProbabilities = new HashSet<>();
    private int entryTotal;
    
    public LootCategory(String name, Rarity rarity, int maxAmountPerChest, List<LootEntry> entries) {
        this.name = name;
        this.rarity = rarity;
        this.entries = entries;
        this.maxAmountPerChest = maxAmountPerChest;
    }
    
    public LootCategory(String name, Rarity rarity, int maxAmountPerChest) {
        this(name, rarity, maxAmountPerChest, new ArrayList<>());
    }
    
    public LootCategory(String name, Rarity rarity) {
        this(name, rarity, 2);
    }
    
    public void addEntries(Rarity rarity, LootItem... items) {
        if (items != null) {
            for (LootItem item : items) {
                this.entries.add(new LootEntry(item, rarity));
            }
        }
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
    
    public void addEntry(LootItem item, Rarity rarity) {
        this.entries.add(new LootEntry(item, rarity));
    }
    
    public void addEntry(Rarity rarity, LootItem item) {
        this.entries.add(new LootEntry(item, rarity));
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
    
    public int getMaxAmountPerChest() {
        return maxAmountPerChest;
    }
    
    public void generateNewProbabilities(Random random) {
        int lastMax = -1;
        for (LootEntry category : this.entries) {
            Rarity rarity = category.getRarity();
            int max = lastMax + NumberHelper.randomInRange(random, rarity.getMin(), rarity.getMax());
            entryProbabilities.add(new Range<>(lastMax + 1, max, category));
            lastMax = max;
        }
        this.entryTotal = lastMax;
    }
    
    public LootEntry generateLoot(Random random) {
        Set<Range<LootEntry>> entryProbabilties = new HashSet<>(this.entryProbabilities);
        int rand = random.nextInt(entryTotal + 1);
        for (Range<LootEntry> range : entryProbabilties) {
            if (range.contains(rand)) {
                return range.object();
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LootCategory that = (LootCategory) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
