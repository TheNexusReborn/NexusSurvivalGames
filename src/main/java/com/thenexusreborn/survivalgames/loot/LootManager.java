package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.tables.*;
import org.bukkit.command.CommandSender;

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
            if (table.getItems().isEmpty()) {
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
    
    public TableParseResult parseTable(CommandSender sender, String raw) {
        if (raw.isBlank()) {
            if (sender != null) {
                MsgType.WARN.send(sender, "Input cannot be blank");
            }
            
            return TableParseResult.INVALID_FORMAT;
        }
        
        if (!raw.contains(":")) {
            if (sender != null) {
                MsgType.WARN.send(sender, "Invalid format. Must be %v", "<loottable>:<amount>");
            }
            
            return TableParseResult.INVALID_FORMAT;
        }
        
        String[] lootSplit = raw.split(":");
        if (lootSplit.length != 2) {
            if (sender != null) {
                MsgType.WARN.send(sender, "Invalid format. Must be %v", "<loottable>:<amount>");
            }
            
            return TableParseResult.INVALID_FORMAT;
        }
        
        SGLootTable lootTable = plugin.getLootManager().getLootTable(lootSplit[0]);
        if (lootTable == null) {
            if (sender != null) {
                MsgType.WARN.send(sender, "Invalid loot table %v", lootSplit[0]);
            }
            
            return TableParseResult.INVALID_TABLE;
        }
        
        int amountOfItems;
        try {
            amountOfItems = Integer.parseInt(lootSplit[1]);
        } catch (NumberFormatException e) {
            if (sender != null) {
                MsgType.WARN.send(sender, "Invalid whole number %v", lootSplit[1]);
            }
            return TableParseResult.INVALID_NUMBER;
        }
        
        return new TableParseResult(lootTable, amountOfItems);
    }
}
