package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.api.util.Range;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class LootTable {
    private final String name;
    private final List<LootCategory> categories;
    
    private Set<Range<LootCategory>> categoryProbabilities = new HashSet<>();
    private int categoryTotal, maxPossibleItems;
    
    public LootTable(String name, List<LootCategory> categories) {
        this.name = name;
        this.categories = categories;
    }
    
    public void generateNewProbabilities(Random random) {
        int lastMax = -1;
        for (LootCategory category : this.categories) {
            category.generateNewProbabilities(random);
            Rarity rarity = category.getRarity();
            int max = lastMax + NumberHelper.randomInRange(random, rarity.getMin(), rarity.getMax());
            categoryProbabilities.add(new Range<>(lastMax + 1, max, category));
            lastMax = max;
        }
        this.categoryTotal = lastMax;
    }
    
    public List<ItemStack> generateLoot(int minAmount, int maxAmount) {
        List<ItemStack> loot = new ArrayList<>();
        
        if (maxAmount < minAmount) {
            return loot;
        }
        
        if (maxAmount > this.maxPossibleItems) {
            return loot;
        }
        
        long start = System.currentTimeMillis();
        
        Random random = new Random();
        
        Set<Range<LootCategory>> categoryProbabilities = new HashSet<>(this.categoryProbabilities);
        
        Map<LootCategory, Integer> categoryAmounts = new HashMap<>();
        int totalItems = random.nextInt(maxAmount - minAmount) + minAmount;
        int itemCount = 0;
        while (itemCount < totalItems) {
            int rand = random.nextInt(categoryTotal + 1);
            for (Range<LootCategory> range : categoryProbabilities) {
                if (range.contains(rand)) {
                    LootCategory category = range.getObject();
                    int current = categoryAmounts.getOrDefault(category, 0);
                    if (current < category.getMaxAmountPerChest()) {
                        categoryAmounts.put(category, current + 1);
                        itemCount++;
                    }
                    break;
                }
            }
        }
    
        for (Entry<LootCategory, Integer> entry : categoryAmounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                loot.add(entry.getKey().generateLoot(random).generateItemStack());
            }
        }
        
        long end = System.currentTimeMillis();
        return loot;
    }
    
    public void addCategory(LootCategory category) {
        this.maxPossibleItems += category.getMaxAmountPerChest();
        this.categories.add(category);
    }
    
    public LootCategory getCategory(String name) {
        for (LootCategory category : this.categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        
        return null;
    }
    
    public LootTable(String name) {
        this(name, new ArrayList<>());
    }
    
    public String getName() {
        return name;
    }
    
    public List<LootCategory> getCategories() {
        return new ArrayList<>(categories);
    }
}
