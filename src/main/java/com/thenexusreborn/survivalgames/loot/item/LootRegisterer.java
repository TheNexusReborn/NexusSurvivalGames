package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.minecraft.colors.ColorHandler;
import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.*;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import org.bukkit.Material;

public class LootRegisterer extends Registerer<LootItem> {
    protected LootRegisterer(IRegistry<LootItem> registry) {
        super(registry);
    }
    
    public RegistryObject<LootItem> register(String name, Material material, LootCategory... categories) {
        LootItem lootItem = new LootItem(name, material);
        lootItem.setCategories(categories);
        return register(Keys.of(ColorHandler.stripColor(name.toLowerCase())), lootItem);
    }
    
    public RegistryObject<LootItem> register(Material material, LootCategory... categories) {
        LootItem lootItem = new LootItem(material);
        lootItem.setCategories(categories);
        return register(Keys.of(material.name().toLowerCase()), lootItem);
    }
}
