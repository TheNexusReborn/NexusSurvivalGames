package com.thenexusreborn.survivalgames.lootv2;

import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootEntry {
    protected final int maxAmount;
    protected final Material material;
    protected final String name;
    protected final Rarity rarity;
    protected final List<String> lore;
    
    public LootEntry(Material material, String name, Rarity rarity, int maxAmount, List<String> lore) {
        this.material = material;
        this.name = name;
        this.rarity = rarity;
        this.maxAmount = maxAmount;
        this.lore = lore;
    }
    
    public LootEntry(Material material, String name, Rarity rarity) {
        this(material, name, rarity, 1, new ArrayList<>());
    }
    
    public LootEntry(Material material, Rarity rarity) {
        this(material, MaterialNames.getDefaultName(material), rarity);
    }
    
    public int getMaxAmount() {
        return maxAmount;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public ItemStack generateItemStack() {
        int amount;
        if (this.maxAmount > 1) {
            amount = new Random().nextInt(maxAmount - 1) + 1;
        } else {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(this.material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!name.equalsIgnoreCase(material.name().replace("_", " "))) {
            itemMeta.setDisplayName(MCUtils.color("&f" + this.name));
        }
        if (this.material == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (material.getMaxDurability() - 4));
        }
        itemMeta.setLore(this.lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
