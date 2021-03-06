package com.thenexusreborn.survivalgames.game;

import org.bukkit.GameMode;

public enum GameTeam {
    TRIBUTES("Tributes", "&a", GameMode.SURVIVAL), 
    SPECTATORS("Spectators", "&c", GameMode.ADVENTURE), //Will be replaced with Adventure after proper spectator system is implemented
    MUTATIONS("Mutations", "&d", GameMode.ADVENTURE), //Not going to be implemented for a while
    ;
    private final String name, color;
    private final GameMode gameMode;
    
    GameTeam(String name, String color, GameMode gameMode) {
        this.name = name;
        this.color = color;
        this.gameMode = gameMode;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public String getJoinMessage() {
        return "&d&l>> &7You joined " + getColor() + getName();    
    }
    
    public String getLeaveMessage() {
        return "&c&l<< &7You left " + getColor() + getName();
    }
}
