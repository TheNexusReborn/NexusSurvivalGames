package com.thenexusreborn.survivalgames.loot.tables;

import com.stardevllc.starlib.random.RandomSelector;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    protected final String name;

    private Map<String, TableItem> registeredItems = new HashMap<>();
    private Map<LootCategory, Integer> categoryAmountOverrides = new EnumMap<>(LootCategory.class);

    public LootTable(String name) {
        this.name = name;
    }
    
    protected void resetItems() {
        this.registeredItems.clear();
    }

    public void addItem(LootItem item, double weight) {
        registeredItems.put(item.getId(), new TableItem(item, weight));
    }

    public void addItems(int weight, LootItem... items) {
        if (items == null) {
            return;
        }

        for (LootItem item : items) {
            addItem(item, weight);
        }
    }
    
    public boolean contains(String itemId) {
        return this.registeredItems.containsKey(itemId);
    }
    
    public void setItemWeight(String itemId, double weight) {
        TableItem tableItem = this.registeredItems.get(itemId);
        if (tableItem != null) {
            tableItem.setWeight(weight);
        }
    }

    public List<TableItem> getItems() {
        return new ArrayList<>(this.registeredItems.values());
    }

    public List<ItemStack> generateLoot(int rolls) {
        List<TableItem> chances = new ArrayList<>(this.registeredItems.values());
        Map<LootCategory, Integer> categoryCounts = new EnumMap<>(LootCategory.class);
        
        List<ItemStack> loot = new ArrayList<>();
        int tries = 0;
        while (loot.size() < rolls && tries < 10) {
            tries++;
            try {
                if (chances.isEmpty()) {
                    SurvivalGames.getInstance().getLogger().severe("Loot Chances are empty.");
                    continue;
                }
                RandomSelector<TableItem> selector = RandomSelector.weighted(chances);
                TableItem lootitem = selector.pick();
                if (lootitem == null) {
                    continue;
                }
                loot.add(lootitem.getItemStack());
                tries = 0;
                for (LootCategory category : lootitem.getCategories()) {
                    categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                }
                
                chances.removeIf(l -> {
                    for (LootCategory category : l.getCategories()) {
                        int categoryMaxAmount = this.categoryAmountOverrides.getOrDefault(category, category.getMaxAmountPerChest());
                        if (categoryMaxAmount > 0) {
                            if (categoryCounts.containsKey(category)) {
                                if (categoryCounts.get(category) >= categoryMaxAmount) {
                                    return true;
                                }
                            }
                        }
                    }
                    
                    return l.getId().equals(lootitem.getId());
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
    
    public void addCategoryAmountOverride(LootCategory category, int amount) {
        this.categoryAmountOverrides.put(category, Math.max(0, amount));
    }

    public double getItemWeight(String name) {
        if (registeredItems.containsKey(name)) {
            return registeredItems.get(name).getWeight();
        }
        
        return 0;
    }
    
    public double getWeightTotal() {
        double total = 0;
        for (TableItem item : registeredItems.values()) {
            total += item.getWeight();
        }
        return total;
    }
}