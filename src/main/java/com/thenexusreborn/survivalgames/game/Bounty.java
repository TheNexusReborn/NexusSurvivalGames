package com.thenexusreborn.survivalgames.game;

import java.util.*;

public final class Bounty {
    private final UUID player;
    private Map<Type, Integer> amounts = new HashMap<>();
    
    public Bounty(UUID player) {
        this.player = player;
    }
    
    public double getAmount(Type type) {
        return amounts.getOrDefault(type, 0);
    }
    
    public void add(Type type, int amount) {
        amounts.put(type, amounts.getOrDefault(type, 0) + amount);
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public enum Type {
        CREDIT, SCORE
    }
}