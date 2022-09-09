package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.nexuscore.util.MaterialNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class LootEntry {
    protected final int maxAmount;
    protected final Rarity rarity;
    protected final LootItem item;
    
    public LootEntry(LootItem item, int maxAmount, Rarity rarity) {
        this.item = item;
        this.maxAmount = maxAmount;
        this.rarity = rarity;
    }
    
    public LootEntry(LootItem item, Rarity rarity) {
        this(item, 1, rarity);
    }
    
    public LootEntry(Material material, String name, Rarity rarity, int maxAmount, List<String> lore) {
        this(new LootItem(material, name, lore), maxAmount, rarity);
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
        return item.getMaterial();
    }
    
    public String getName() {
        return item.getName();
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public List<String> getLore() {
        return item.getLore();
    }
    
    public ItemStack generateItemStack() {
        ItemStack itemStack = item.getItemStack();
        int amount;
        if (this.maxAmount > 1) {
            amount = new Random().nextInt(maxAmount - 1) + 1;
        } else {
            amount = 1;
        }
        itemStack.setAmount(amount);
        return itemStack;
    }
}
