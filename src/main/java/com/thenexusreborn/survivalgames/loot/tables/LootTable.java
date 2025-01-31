package com.thenexusreborn.survivalgames.loot.tables;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    protected final String name;

    private LootItem[] items = new LootItem[0];
    private int lastIndex = 0;

    protected final Map<String, Integer> itemWeights = new HashMap<>();

    public LootTable(String name) {
        this.name = name;
    }
    
    protected void resetItems() {
        this.items = new LootItem[0];
        this.lastIndex = 0;
        this.itemWeights.clear();
    }

    public void addItem(LootItem item, int weight) {
        int targetLength = weight + items.length;
        if (items.length < targetLength) {
            LootItem[] newItems = new LootItem[targetLength];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }

        for (int w = 0; w < weight; w++) {
            items[lastIndex] = item;
            lastIndex++;
        }

        String normalizedName = StarColors.stripColor(item.getName()).replace(" ", "_").replace("'", "").toLowerCase();
        if (itemWeights.containsKey(normalizedName)) {
            itemWeights.put(normalizedName, itemWeights.get(normalizedName) + weight);
        } else {
            itemWeights.put(normalizedName, weight);
        }
    }

    public void addItems(int weight, LootItem... items) {
        if (items == null) {
            return;
        }

        int targetLength = this.items.length + (weight * items.length);
        if (this.items.length < targetLength) {
            LootItem[] newItems = new LootItem[targetLength];
            System.arraycopy(this.items, 0, newItems, 0, this.items.length);
            this.items = newItems;
        }

        for (LootItem item : items) {
            addItem(item, weight);
        }
    }

    public LootItem[] getItems() {
        LootItem[] items = new LootItem[this.items.length];
        System.arraycopy(this.items, 0, items, 0, this.items.length);
        return items;
    }

    public List<ItemStack> generateLoot(int rolls) {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < rolls; i++) {
            loot.add(items[random.nextInt(items.length)].getItemStack());
        }

        return loot;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getItemWeights() {
        return itemWeights;
    }
}