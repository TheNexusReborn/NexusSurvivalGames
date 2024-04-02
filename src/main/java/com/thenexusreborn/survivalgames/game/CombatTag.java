package com.thenexusreborn.survivalgames.game;

import com.stardevllc.starlib.time.TimeUnit;

import java.util.UUID;

public final class CombatTag {
    private UUID player, other;
    private long timestamp;
    private Game game;
    
    public CombatTag(Game game, UUID player, UUID other, long timestamp) {
        this.game = game;
        this.player = player;
        this.other = other;
        this.timestamp = timestamp;
    }
    
    public CombatTag(Game game, UUID player, UUID other) {
        this(game, player, other, System.currentTimeMillis());
    }
    
    public CombatTag(Game game, UUID player) {
        this(game, player, null);
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
        return this.other != null && System.currentTimeMillis() < this.timestamp + TimeUnit.SECONDS.toMillis(game.getSettings().getCombatTagLength());
    }

    @Override
    public String toString() {
        return "CombatTag{" +
                "player=" + player +
                ", other=" + other +
                ", timestamp=" + timestamp +
                '}';
    }
}
