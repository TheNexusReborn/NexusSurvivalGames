package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.survivalgames.loot.tables.*;

import java.util.*;

public class LootManager {
    
    private final List<LootTable> lootTables = new ArrayList<>();
    
    private static final LootManager instance = new LootManager();
    
    public static LootManager getInstance() {
        return instance;
    }
    
    public LootManager() {
        this.lootTables.add(new TierOneLootTable());
        this.lootTables.add(new TierTwoLootTable());
        this.lootTables.add(new TierThreeLootTable());
        this.lootTables.add(new TierFourLootTable());
    }
    
    public List<LootTable> getLootTables() {
        return lootTables;
    }
    
    public LootTable getLootTable(String name) {
        for (LootTable lootTable : this.lootTables) {
            if (lootTable.getName().equalsIgnoreCase(name)) {
                return lootTable;
            }
        }
        
        return null;
    }
}
