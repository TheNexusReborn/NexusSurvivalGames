package com.thenexusreborn.survivalgames.game;

import java.util.UUID;

public final class CombatTag {
    private UUID player, other;
    private long timestamp;
    
    public CombatTag(UUID player, UUID other, long timestamp) {
        this.player = player;
        this.other = other;
        this.timestamp = timestamp;
    }
    
    public CombatTag(UUID player, UUID other) {
        this(player, other, System.currentTimeMillis());
    }
    
    public CombatTag(UUID player) {
        this(player, null);
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public UUID getOther() {
        return other;
    }
    
    public void setOther(UUID other) {
        this.other = other;
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isInCombatWith(UUID uuid) {
        if (!isInCombat()) {
            return false;
        }
        
        return this.other.equals(uuid);
    }
    
    public boolean isInCombat() {
        return this.other != null && System.currentTimeMillis() < this.timestamp + 10000;
    }
}
