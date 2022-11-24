package com.thenexusreborn.survivalgames.game;

import org.bukkit.*;

public enum GameTeam {
    TRIBUTES("Tributes", "&a", "&c", GameMode.SURVIVAL, Sound.WITHER_SPAWN), 
    SPECTATORS("Spectators", "&c", GameMode.ADVENTURE, Sound.ARROW_HIT), 
    MUTATIONS("Mutations", "&d", GameMode.SURVIVAL, Sound.ZOMBIE_PIG_DEATH), 
    ZOMBIES("Zombies", "&2", GameMode.SURVIVAL, Sound.ZOMBIE_DEATH);
    private final String name, color, remainColor;
    private final GameMode gameMode;
    private final Sound deathSound;
    
    GameTeam(String name, String color, GameMode gameMode, Sound sound) {
        this(name, color, color, gameMode, sound);
    }
    
    GameTeam(String name, String color, String remainColor, GameMode gameMode, Sound deathSound) {
        this.name = name;
        this.color = color;
        this.remainColor = remainColor;
        this.gameMode = gameMode;
        this.deathSound = deathSound;
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
    
    public String getRemainColor() {
        return remainColor;
    }
    
    public Sound getDeathSound() {
        return deathSound;
    }
    
    public String getJoinMessage() {
        return "&a&l>> &7You joined " + getColor() + getName();    
    }
    
    public String getLeaveMessage() {
        return "&c&l<< &7You left " + getColor() + getName();
    }
}
