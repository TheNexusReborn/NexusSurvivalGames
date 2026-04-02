package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.HashRegistry;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

public class ItemRegistry extends HashRegistry<LootItem> {
    
    private final Set<Material> materials = EnumSet.noneOf(Material.class);
    
    public ItemRegistry() {
        super(LootItem.class, Keys.of("sg_loot"), "SG Loot", null, false, null, null);
        
        addRegisterListener(e -> materials.add(e.value().getMaterial()));
        addRemoveListener(e -> materials.remove(e.value().getMaterial()));
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
