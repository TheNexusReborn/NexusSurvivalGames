package com.thenexusreborn.survivalgames.game;

import java.util.*;

public class DamageInfo {
    private UUID player;
    private List<UUID> damagers = new ArrayList<>();
    
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
}
