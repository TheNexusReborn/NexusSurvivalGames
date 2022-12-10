package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.storage.annotations.*;
import com.thenexusreborn.api.helper.ReflectionHelper;
import com.thenexusreborn.survivalgames.newsettings.collection.SettingList;
import com.thenexusreborn.survivalgames.newsettings.object.impl.LobbySetting;

import java.lang.reflect.Field;

@TableInfo("sglobbysettings")
public class LobbySettings extends SettingList<LobbySetting> {
    private int voteStartThreshold = 2;
    private boolean voteWeight = true;
    private boolean keepPreviousGameSettings = true;
    private boolean sounds = true;
    
    public LobbySettings() {
        super("lobby");
    }
    
    public LobbySettings(String type) {
        super(type);
    }
    
    public int getMaxPlayers() {
        return get("max_players").getValue().getAsInt();
    }
    
    public LobbySettings setMaxPlayers(int maxPlayers) {
        get("max_players").getValue().set(maxPlayers);
        return this;
    }
    
    public int getMinPlayers() {
        return get("min_players").getValue().getAsInt();
    }
    
    public LobbySettings setMinPlayers(int minPlayers) {
        get("min_players").getValue().set(minPlayers);
        return this;
    }
    
    public int getMaxGames() {
        return get("max_games").getValue().getAsInt();
    }
    
    public LobbySettings setMaxGames(int maxGames) {
        get("max_games").getValue().set(maxGames);
        return this;
    }
    
    public int getTimerLength() {
        return get("timer_length").getValue().getAsInt();
    }
    
    public LobbySettings setTimerLength(int timerLength) {
        get("timer_length").getValue().set(timerLength);
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
    
    public int getVoteStartThreshold() {
        return voteStartThreshold;
    }
    
    public LobbySettings setVoteStartThreshold(int voteStartThreshold) {
        this.voteStartThreshold = voteStartThreshold;
        return this;
    }
    
    @Override
    public LobbySettings clone() {
        LobbySettings settings = new LobbySettings();
    
        for (Field field : ReflectionHelper.getClassFields(getClass())) {
            field.setAccessible(true);
            try {
                field.set(settings, field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return settings;
    }
}
