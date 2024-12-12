package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starcore.color.ColorHandler;

public class ItemRegistry extends StringRegistry<LootItem> {

    public ItemRegistry() {
        super(null, string -> ColorHandler.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getName, null, null);
    }
}
