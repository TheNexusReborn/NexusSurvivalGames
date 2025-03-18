package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;

public class TableParseResult {
    
    public static final TableParseResult INVALID_FORMAT = new TableParseResult() {
        public boolean invalidFormat() {
            return true;
        }
    };
    
    public static final TableParseResult INVALID_TABLE = new TableParseResult() {
        public boolean invalidTable() {
            return true;
        }
    };
    
    public static final TableParseResult INVALID_NUMBER = new TableParseResult() {
        public boolean invalidNumber() {
            return true;
        }
    };
    
    public static final TableParseResult EMPTY = new TableParseResult();
    
    private SGLootTable lootTable;
    private int amount;
    
    private TableParseResult() {}
    
    public TableParseResult(SGLootTable lootTable, int amount) {
        this.lootTable = lootTable;
        this.amount = amount;
    }
    
    public SGLootTable getLootTable() {
        return lootTable;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public boolean invalidFormat() {
        return false;
    }
    
    public boolean invalidNumber() {
        return false;
    }
    
    public boolean invalidTable() {
        return false;
    }
}
