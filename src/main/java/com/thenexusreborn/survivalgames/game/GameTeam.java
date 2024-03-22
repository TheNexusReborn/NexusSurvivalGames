package com.thenexusreborn.survivalgames.game;

import org.bukkit.*;

public enum GameTeam {
    TRIBUTES("Tributes", "&a", "&c", GameMode.SURVIVAL, Sound.WITHER_SPAWN, "&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: {message}"), 
    SPECTATORS("Spectators", "&c", GameMode.ADVENTURE, Sound.ARROW_HIT, "&8[&cSpectators&8] &r%nexussg_displayname%&8: {message}"), 
    MUTATIONS("Mutations", "&d", GameMode.SURVIVAL, Sound.ZOMBIE_PIG_DEATH, "&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: {message}"), 
    ZOMBIES("Zombies", "&2", GameMode.SURVIVAL, Sound.ZOMBIE_DEATH, "%&8<&3nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: {message}");
    private final String name, color, remainColor, chatFormat;
    private final GameMode gameMode;
    private final Sound deathSound;
    
    GameTeam(String name, String color, GameMode gameMode, Sound sound, String chatFormat) {
        this(name, color, color, gameMode, sound, chatFormat);
    }
    
    GameTeam(String name, String color, String remainColor, GameMode gameMode, Sound deathSound, String chatFormat) {
        this.name = name;
        this.color = color;
        this.remainColor = remainColor;
        this.gameMode = gameMode;
        this.deathSound = deathSound;
        this.chatFormat = chatFormat;
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
    
    public String getChatFormat() {
        return chatFormat;
    }
    
    public String getJoinMessage() {
        return "&a&l>> &7You joined " + getColor() + getName();    
    }
    
    public String getLeaveMessage() {
        return "&c&l<< &7You left " + getColor() + getName();
    }
}
