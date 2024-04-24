package com.thenexusreborn.survivalgames.loot;

import com.stardevllc.starcore.color.ColorUtils;
import com.stardevllc.starcore.utils.MaterialNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
            itemMeta.setDisplayName(ColorUtils.color("&f" + this.getName()));
        }
        
        if (this.getMaterial() == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (getMaterial().getMaxDurability() - 4));
        }
        
        List<String> lore = new LinkedList<>();
        for (String line : this.getLore()) {
            lore.add(ColorUtils.color(line));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
