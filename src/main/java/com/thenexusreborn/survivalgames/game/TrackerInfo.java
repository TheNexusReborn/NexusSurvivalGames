package com.thenexusreborn.survivalgames.game;

public class TrackerInfo {
    private final String target;
    private final int distance;
    private final String health;
    private final String maxHealth;
    
    public TrackerInfo(String target, int distance, String health, String maxHealth) {
        this.target = target;
        this.distance = distance;
        this.health = health;
        this.maxHealth = maxHealth;
    }
    
    public String getTarget() {
        return target;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public String getHealth() {
        return health;
    }
    
    public String getMaxHealth() {
        return maxHealth;
    }
}
