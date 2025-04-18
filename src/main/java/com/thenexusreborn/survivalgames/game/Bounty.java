package com.thenexusreborn.survivalgames.game;

import java.util.*;

public final class Bounty {
    private final UUID player;
    private Map<Type, Integer> amounts = new EnumMap<>(Type.class);
    
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

    public void remove(Type type) {
        this.amounts.remove(type);
    }
    
    public boolean has() {
        if (amounts.isEmpty()) {
            return false;
        }
        
        int total = 0;
        for (Integer amount : amounts.values()) {
            total += amount;
        }
        
        return total != 0;
    }

    public enum Type {
        CREDITS, SCORE
    }

    @Override
    public String toString() {
        return "Bounty{" +
                "player=" + player +
                ", amounts=" + amounts +
                '}';
    }
}
