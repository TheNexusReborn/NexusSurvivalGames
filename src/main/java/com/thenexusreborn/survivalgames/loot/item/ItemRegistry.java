package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starlib.registry.StringRegistry;
import com.stardevllc.starcore.api.StarColors;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

public class ItemRegistry extends StringRegistry<LootItem> {
    
    private Set<Material> materials = EnumSet.noneOf(Material.class);
    
    public ItemRegistry() {
        super(null, string -> StarColors.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getId, null, null);
        addRegisterListener((s, lootItem) -> materials.add(lootItem.getMaterial()));
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
    
    public Set<Material> getMaterials() {
        return materials;
    }
    
    public LootItem getByMaterial(Material material) {
        for (LootItem item : this.getObjects().values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        
        return null;
    }
}
