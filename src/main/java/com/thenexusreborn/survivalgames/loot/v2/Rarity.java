package com.thenexusreborn.survivalgames.loot.v2;

public enum Rarity {
    COMMON(100, 200), 
    UNCOMMON(50, 75), 
    RARE(15, 25),
    EPIC(5, 10), 
    LEGENDARY(0, 3);
    
    private final int min, max;
    
    Rarity(int min, int max) {
        this.min = min;
        this.max = max;
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
}
