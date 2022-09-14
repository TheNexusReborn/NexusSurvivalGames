package com.thenexusreborn.survivalgames.loot;

public enum Rarity {
    COMMON(30, 60), 
    UNCOMMON(12, 23), 
    RARE(6, 11),
    EPIC(2, 5), 
    LEGENDARY(0, 1);
    
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
