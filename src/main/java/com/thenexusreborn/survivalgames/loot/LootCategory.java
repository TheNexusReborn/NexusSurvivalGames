package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.survivalgames.SurvivalGames;
import me.firestar311.starlib.api.range.Range;
import org.bukkit.Material;

import java.util.*;

public class LootCategory {
    private final String name;
    private final int weight;
    private final List<LootEntry> entries;
    private final int maxAmountPerChest;
    
    private final Set<Range<LootEntry>> entryProbabilities = new HashSet<>();
    private int entryTotal;
    
    public LootCategory(String name, int weight, int maxAmountPerChest, List<LootEntry> entries) {
        this.name = name;
        this.weight = weight;
        this.entries = entries;
        this.maxAmountPerChest = maxAmountPerChest;
    }
    
    public LootCategory(String name, int weight, int maxAmountPerChest) {
        this(name, weight, maxAmountPerChest, new ArrayList<>());
    }
    
    public LootCategory(String name, int weight) {
        this(name, weight, 2);
    }
    
    public void addEntries(int weight, LootItem... items) {
        if (items != null) {
            for (LootItem item : items) {
                this.entries.add(new LootEntry(item, weight));
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
    
    public void addEntry(int rarity, LootItem item) {
        this.entries.add(new LootEntry(item, rarity));
    }
    
    public void addEntry(LootEntry lootEntry) {
        this.entries.add(lootEntry);
    }
    
    public String getName() {
        return name;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public List<LootEntry> getEntries() {
        return entries;
    }
    
    public int getMaxAmountPerChest() {
        return maxAmountPerChest;
    }
    
    public void generateNewProbabilities(Random random) {
        int index = 0;
        for (LootEntry entry : this.entries) {
            int min = index;
            index += entry.getWeight();
            entryProbabilities.add(new Range<>(min, index, entry));
            index++;
        }
        this.entryTotal = index;
    }
    
    public LootEntry generateLoot(Random random) {
        Set<Range<LootEntry>> entryProbabilties = new HashSet<>(this.entryProbabilities);
        int rand = random.nextInt(entryTotal + 1);
        for (Range<LootEntry> range : entryProbabilties) {
            if (range.contains(rand)) {
                if (range.object() == null) {
                    SurvivalGames.getPlugin(SurvivalGames.class).getLogger().severe("A range has a null object " + range.min() + " - " + range.max());
                }
                return range.object();
            }
        }
        
        SurvivalGames.getPlugin(SurvivalGames.class).getLogger().severe("Could not find an object for the generated number " + rand);
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
