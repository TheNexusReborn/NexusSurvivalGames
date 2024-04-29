package com.thenexusreborn.survivalgames.loot;

import com.stardevllc.starcore.color.ColorUtils;
import com.stardevllc.starlib.registry.StringRegistry;

public class ItemRegistry extends StringRegistry<LootItem> {

    public ItemRegistry() {
        super(string -> ColorUtils.stripColor(string.toLowerCase().replace(" ", "_")), LootItem::getName);
    }
}
