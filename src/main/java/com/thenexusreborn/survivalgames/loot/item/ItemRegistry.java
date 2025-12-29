package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starlib.objects.registry.Registry;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starlib.objects.registry.RegistryObject;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

public class ItemRegistry extends Registry<String, LootItem> {
    
    private Set<Material> materials = EnumSet.noneOf(Material.class);
    
    public ItemRegistry() {
        super(null, string -> StarColors.stripColor(string.toLowerCase().replace(" ", "_").replace("'", "")), LootItem::getId, null, null);
        addListener(c -> {
            if (c.added() != null) {
                materials.add(c.added().getMaterial());
            } else if (c.removed() != null) {
                materials.remove(c.removed().getMaterial());
            }
        });
    }
    
    public RegistryObject<String, LootItem> register(Material material, LootCategory... categories) {
        LootItem object = new LootItem(material);
        object.setCategories(categories);
        return register(object);
    }
    
    public RegistryObject<String, LootItem> register(String name, Material material, LootCategory... categories) {
        LootItem object = new LootItem(name, material);
        object.setCategories(categories);
        return register(object);
    }
    
    public RegistryObject<String, LootItem> register(String id, String name, Material material, LootCategory... categories) {
        LootItem object = new LootItem(id, name, material);
        object.setCategories(categories);
        return register(object);
    }
    
    public Set<Material> getMaterials() {
        return materials;
    }
    
    public LootItem getByMaterial(Material material) {
        for (LootItem item : this.values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        
        return null;
    }
}
