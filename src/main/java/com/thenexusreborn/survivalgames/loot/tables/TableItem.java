package com.thenexusreborn.survivalgames.loot.tables;

import com.stardevllc.starlib.random.Weighted;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class TableItem implements Weighted {
    
    private LootItem item;
    private double weight;
    
    public TableItem(LootItem item, double weight) {
        this.item = item;
        this.weight = weight;
    }
    
    public LootItem getItem() {
        return item;
    }
    
    public String getName() {
        return item.getName();
    }
    
    public String getId() {
        return item.getId(); 
    }
    
    public ItemStack getItemStack() {
        return item.getItemStack();
    }
    
    public Set<LootCategory> getCategories() {
        return item.getCategories();
    }
    
    @Override
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
