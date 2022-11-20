package com.thenexusreborn.survivalgames.game;

import java.util.UUID;

public final class CombatTag {
    private UUID player, target;
    private long timestamp;
    
    public CombatTag(UUID player, UUID target, long timestamp) {
        this.player = player;
        this.target = target;
        this.timestamp = timestamp;
    }
    
    public CombatTag(UUID player, UUID target) {
        this(player, target, System.currentTimeMillis());
    }
    
    public CombatTag(UUID player) {
        this(player, null);
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public void setTarget(UUID target) {
        this.target = target;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
