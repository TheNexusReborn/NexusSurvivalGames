package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.nexuscore.util.MaterialNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootEntry {
    protected final int maxAmount;
    protected final int weight;
    protected final LootItem item;
    
    public LootEntry(LootItem item, int maxAmount, int weight) {
        this.item = item;
        this.maxAmount = maxAmount;
        this.weight = weight;
    }
    
    public LootEntry(LootItem item, int weight) {
        this(item, 1, weight);
    }
    
    public LootEntry(Material material, String name, int weight, int maxAmount, List<String> lore) {
        this(new LootItem(material, name, lore), maxAmount, weight);
    }
    
    public LootEntry(Material material, String name, int weight) {
        this(material, name, weight, 1, new ArrayList<>());
    }
    
    public LootEntry(Material material, int weight) {
        this(material, MaterialNames.getDefaultName(material), weight);
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
    
    public int getWeight() {
        return weight;
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
