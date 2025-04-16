package com.thenexusreborn.survivalgames.game;

import java.util.*;

public class DamageInfo {
    private UUID player;
    private Set<UUID> damagers = new HashSet<>();
    
    public DamageInfo(UUID player) {
        this.player = player;
    }
    
    public void addDamager(UUID damager) {
        this.damagers.add(damager);
    }
    
    public void clearDamagers() {
        this.damagers.clear();
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public List<UUID> getDamagers() {
        return new ArrayList<>(damagers);
    }
    
    public boolean hasDamagers() {
        return !damagers.isEmpty();
    }

    @Override
    public String toString() {
        return "DamageInfo{" +
                "player=" + player +
                ", damagers=" + damagers +
                '}';
    }
}
