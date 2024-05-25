package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.registry.StringRegistry;

public class ItemRegistry extends StringRegistry<LootItem> {

    public ItemRegistry() {
        super(null, string -> ColorHandler.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getName, null, null);
    }
}
