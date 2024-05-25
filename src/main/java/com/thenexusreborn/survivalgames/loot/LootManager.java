package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.tables.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LootManager {
    
    private final SurvivalGames plugin;
    private final List<SGLootTable> lootTables = new ArrayList<>();
    
    private File tableFolder;
    
    public LootManager(SurvivalGames plugin) {
        this.plugin = plugin;
        
        this.tableFolder = new File(plugin.getDataFolder(), "loottables");
        if (!tableFolder.exists()) {
            tableFolder.mkdir();
        }
    }
    
    public void loadData() {
        this.lootTables.add(new TierOneLootTable(tableFolder));
        this.lootTables.add(new TierTwoLootTable(tableFolder));
        this.lootTables.add(new TierThreeLootTable(tableFolder));
        this.lootTables.add(new TierFourLootTable(tableFolder));
        
        this.lootTables.forEach(table -> {
            table.loadData();
            if (table.getItemWeights().isEmpty()) {
                table.loadDefaultData();
            }
        });
    }
    
    public void saveData() {
        this.lootTables.forEach(SGLootTable::saveData);
    }
    
    public List<SGLootTable> getLootTables() {
        return lootTables;
    }
    
    public SGLootTable getLootTable(String name) {
        for (SGLootTable lootTable : this.lootTables) {
            if (lootTable.getName().equalsIgnoreCase(name)) {
                return lootTable;
            }
        }
        
        return null;
    }
}
