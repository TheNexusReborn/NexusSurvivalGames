package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starcore.api.StarColors;

public class ItemRegistry extends StringRegistry<LootItem> {

    public ItemRegistry() {
        super(null, string -> StarColors.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getName, null, null);
    }
}
