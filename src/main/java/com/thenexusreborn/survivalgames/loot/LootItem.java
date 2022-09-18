package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootItem {
    protected final Material material;
    protected final String name;
    protected final List<String> lore = new LinkedList<>();
    
    public LootItem(Material material, String name, List<String> lore) {
        this.material = material;
        this.name = name;
        this.lore.addAll(lore);
        
        Items.REGISTRY.add(this);
    }
    
    public LootItem(Material material, String name) {
        this(material, name, new ArrayList<>());
    }
    
    public LootItem(Material material) {
        this(material, MaterialNames.getDefaultName(material));
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }
    
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!getName().equalsIgnoreCase(getMaterial().name().replace("_", " "))) {
            itemMeta.setDisplayName(MCUtils.color("&f" + this.getName()));
        }
        
        if (this.getMaterial() == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (getMaterial().getMaxDurability() - 4));
        }
        
        List<String> lore = new LinkedList<>();
        for (String line : this.getLore()) {
            lore.add(MCUtils.color(line));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
