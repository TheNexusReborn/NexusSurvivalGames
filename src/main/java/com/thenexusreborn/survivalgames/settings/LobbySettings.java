package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.data.annotations.*;

import java.lang.reflect.Field;

@SuppressWarnings("DuplicatedCode")
@TableInfo("sglobbysettings")
public class LobbySettings {
    @Primary
    private long id;
    private String type = "default";
    private int maxPlayers = 24;
    private int minPlayers = 4;
    private int maxGames = 10;
    private int timerLength = 30;
    private boolean voteWeight = true;
    private boolean keepPreviousGameSettings = true;
    private boolean sounds = true;
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
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
    
    @Override
    public String toString() {
        return "LobbySettings{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", minPlayers=" + minPlayers +
                ", maxGames=" + maxGames +
                ", timerLength=" + timerLength +
                ", voteWeight=" + voteWeight +
                ", keepPreviousGameSettings=" + keepPreviousGameSettings +
                ", sounds=" + sounds +
                '}';
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
