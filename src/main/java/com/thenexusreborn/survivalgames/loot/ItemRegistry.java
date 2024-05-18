package com.thenexusreborn.survivalgames.loot;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.registry.StringRegistry;

public class ItemRegistry extends StringRegistry<LootItem> {

    public ItemRegistry() {
        super(null, string -> ColorHandler.stripColor(string.toLowerCase().replace(" ", "_")), LootItem::getName, null, null);
    }
}
