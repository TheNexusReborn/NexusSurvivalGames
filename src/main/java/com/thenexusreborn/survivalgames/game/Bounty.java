package com.thenexusreborn.survivalgames.game;

import java.util.*;

public final class Bounty {
    private final UUID player;
    private final Type type;
    private double amount;
    
    public Bounty(UUID player, double amount, Type type) {
        this.player = player;
        this.amount = amount;
        this.type = type;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void add(double value) {
        this.amount += value;
    }
    
    public Type getType() {
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bounty bounty = (Bounty) o;
        return Objects.equals(player, bounty.player) && type == bounty.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(player, type);
    }
    
    @Override
    public String toString() {
        return "Bounty[" +
                "player=" + player + ", " +
                "amount=" + amount + ", " +
                "type=" + type + ']';
    }
    
    public enum Type {
        CREDIT, SCORE
    }
}
