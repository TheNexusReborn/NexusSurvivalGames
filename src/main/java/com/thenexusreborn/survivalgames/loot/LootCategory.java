package com.thenexusreborn.survivalgames.loot;

import java.util.Objects;

public class LootCategory {
    private final String name;
    private int maxAmountPerChest;

    public LootCategory(String name) {
        this.name = name;
        
        Categories.REGISTRY.register(this);
    }

    public LootCategory(String name, int maxAmountPerChest) {
        this(name);
        this.maxAmountPerChest = maxAmountPerChest;
    }

    public String getName() {
        return name;
    }

    public int getMaxAmountPerChest() {
        return maxAmountPerChest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LootCategory that = (LootCategory) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
