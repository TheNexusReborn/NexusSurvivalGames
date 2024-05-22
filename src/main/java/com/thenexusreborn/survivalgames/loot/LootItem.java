package com.thenexusreborn.survivalgames.loot;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starcore.utils.MaterialNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LootItem {
    protected final LootCategory category;
    protected final Material material;
    protected final String name;
    protected final List<String> lore = new LinkedList<>();
    
    public LootItem(LootCategory category, Material material, String name, List<String> lore) {
        this.category = category;
        this.material = material;
        this.name = name;
        this.lore.addAll(lore);
        
        Items.REGISTRY.register(this);
    }
    
    public LootItem(LootCategory category, Material material, String name) {
        this(category, material, name, new ArrayList<>());
    }
    
    public LootItem(LootCategory category, Material material) {
        this(category, material, MaterialNames.getDefaultName(material));
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
            itemMeta.setDisplayName(ColorHandler.getInstance().color("&f" + this.getName()));
        }
        
        if (this.getMaterial() == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (getMaterial().getMaxDurability() - 4));
        }
        
        List<String> lore = new LinkedList<>();
        for (String line : this.getLore()) {
            lore.add(ColorHandler.getInstance().color(line));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public LootCategory getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        LootItem lootItem = (LootItem) object;
        return Objects.equals(name, lootItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
