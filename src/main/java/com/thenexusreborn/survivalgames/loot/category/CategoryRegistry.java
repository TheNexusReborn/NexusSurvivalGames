package com.thenexusreborn.survivalgames.loot.category;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.registry.StringRegistry;

public class CategoryRegistry extends StringRegistry<LootCategory> {

    public CategoryRegistry() {
        super(null, string -> ColorHandler.stripColor(string.toLowerCase().replace(" ", "_")), LootCategory::getName, null, null);
    }
}
