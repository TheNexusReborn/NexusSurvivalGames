package com.thenexusreborn.survivalgames.settings;

import java.lang.reflect.Field;

@SuppressWarnings("DuplicatedCode")
public class LobbySettings extends SGSettings {
    private int id;
    private String type = "default";
    private int maxPlayers = 24;
    private int minPlayers = 4;
    private int maxGames = 10;
    private int timerLength = 30;
    private boolean voteWeight = true;
    private boolean keepPreviousGameSettings = true;
    private boolean sounds = true;
    
    public LobbySettings() {
        super("sglobbysettings");
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LobbySettings setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public LobbySettings setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }
    
    public int getMaxGames() {
        return maxGames;
    }
    
    public LobbySettings setMaxGames(int maxGames) {
        this.maxGames = maxGames;
        return this;
    }
    
    public int getTimerLength() {
        return timerLength;
    }
    
    public LobbySettings setTimerLength(int timerLength) {
        this.timerLength = timerLength;
        return this;
    }
    
    public boolean isVoteWeight() {
        return voteWeight;
    }
    
    public LobbySettings setVoteWeight(boolean voteWeight) {
        this.voteWeight = voteWeight;
        return this;
    }
    
    public boolean isKeepPreviousGameSettings() {
        return keepPreviousGameSettings;
    }
    
    public LobbySettings setKeepPreviousGameSettings(boolean keepPreviousGameSettings) {
        this.keepPreviousGameSettings = keepPreviousGameSettings;
        return this;
    }
    
    public boolean isSounds() {
        return sounds;
    }
    
    public LobbySettings setSounds(boolean sounds) {
        this.sounds = sounds;
        return this;
    }
    
    public static LobbySettings loadFromDatabase(String type) {
        //TODO
        return null;
    }
    
    @Override
    public LobbySettings clone() {
        LobbySettings settings = new LobbySettings();
    
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                field.set(settings, field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        settings.setId(0);
        return settings;
    }
}
