package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.LootCategory;
import com.thenexusreborn.survivalgames.loot.LootTable;

public abstract class CommonLootTable extends LootTable {

    protected LootCategory cookedFood;
    protected LootCategory rawFood;
    protected LootCategory miscFood;
    protected LootCategory armor;
    protected LootCategory weapons;
    protected LootCategory components;
    protected LootCategory throwables;
    protected LootCategory placeables;
    protected LootCategory tools;
    
    public CommonLootTable(String name) {
        super(name);
    }
    
    protected void registerCategories() {
        addCategory(cookedFood);
        addCategory(rawFood);
        addCategory(miscFood);
        addCategory(armor);
        addCategory(weapons);
        addCategory(components);
        addCategory(throwables);
        addCategory(placeables);
        addCategory(tools);
    }
}
