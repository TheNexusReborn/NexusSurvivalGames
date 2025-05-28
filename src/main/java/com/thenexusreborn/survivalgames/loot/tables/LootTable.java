package com.thenexusreborn.survivalgames.loot.tables;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    protected final String name;

    private LootItem[] items = new LootItem[0];
    private int lastIndex;
    
    private Map<String, LootItem> registeredItems = new HashMap<>();

    protected final Map<String, Integer> itemWeights = new HashMap<>();

    public LootTable(String name) {
        this.name = name;
    }
    
    protected void resetItems() {
        this.items = new LootItem[0];
        this.lastIndex = 0;
        this.itemWeights.clear();
        this.registeredItems.clear();
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
        
        registeredItems.put(normalizedName, item);
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
        List<LootItem> chances = new ArrayList<>(Arrays.asList(this.items));
        Map<LootCategory, Integer> categoryCounts = new EnumMap<>(LootCategory.class);
        
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < rolls; i++) {
            try {
                int index = random.nextInt(chances.size());
                LootItem lootitem = chances.get(index);
                if (lootitem == null) {
                    SurvivalGames.getInstance().getLogger().warning("Loot item was null for random index " + index);
                    continue;
                }
                loot.add(lootitem.getItemStack());
                categoryCounts.put(lootitem.getCategory(), categoryCounts.getOrDefault(lootitem.getCategory(), 0) + 1);
                chances.removeIf(l -> {
                    if (l.getCategory().getMaxAmountPerChest() > 0) {
                        if (categoryCounts.containsKey(l.getCategory())) {
                            if (categoryCounts.get(l.getCategory()) >= l.getCategory().getMaxAmountPerChest()) {
                                return true;
                            }
                        }
                    }
                    
                    return l.getName().equals(lootitem.getName());
                });
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return loot;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getItemWeights() {
        return itemWeights;
    }
    
    public LootItem getItem(String name) {
        return registeredItems.get(StarColors.stripColor(name).replace(" ", "_").replace("'", "").toLowerCase());
    }
    
    public int getItemWeight(String name) {
        String normalizedName = StarColors.stripColor(name).replace(" ", "_").replace("'", "").toLowerCase();
        if (itemWeights.containsKey(normalizedName)) {
            return itemWeights.get(normalizedName);
        }
        
        return 0;
    }
    
    public int getWeightTotal() {
        int total = 0;
        for (Integer weight : itemWeights.values()) {
            total += weight;
        }
        return total;
    }
}