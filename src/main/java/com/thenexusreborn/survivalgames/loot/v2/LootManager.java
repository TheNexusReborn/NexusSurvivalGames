package com.thenexusreborn.survivalgames.loot.v2;

import com.thenexusreborn.survivalgames.loot.v2.tables.TierOneLootTable;

import java.util.*;

public class LootManager {
    
    private final List<LootTable> lootTables = new ArrayList<>();
    
    private static final LootManager instance = new LootManager();
    
    public static LootManager getInstance() {
        return instance;
    }
    
    public LootManager() {
        LootTable tierOne = new TierOneLootTable();
        this.lootTables.add(tierOne);
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
