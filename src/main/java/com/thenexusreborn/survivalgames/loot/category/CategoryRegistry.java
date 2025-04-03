package com.thenexusreborn.survivalgames.loot.category;

import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starcore.StarColors;

public class CategoryRegistry extends StringRegistry<LootCategory> {

    public CategoryRegistry() {
        super(null, string -> StarColors.stripColor(string.toLowerCase().replace(" ", "_")), LootCategory::getName, null, null);
    }
}
