package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starcore.api.StarColors;
import org.bukkit.Material;

public class ItemRegistry extends StringRegistry<LootItem> {
    public ItemRegistry() {
        super(null, string -> StarColors.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getId, null, null);
    }
    
    public LootItem register(Material material) {
        return register(new LootItem(material));
    }
    
    public LootItem register(String name, Material material) {
        return register(new LootItem(name, material));
    }
    
    public LootItem register(String id, String name, Material material) {
        return register(new LootItem(id, name, material));
    }
}
