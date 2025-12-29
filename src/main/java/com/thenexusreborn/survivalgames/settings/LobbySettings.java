package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

@TableName("sglobbysettings")
public class LobbySettings implements Cloneable, ISettings {
    @PrimaryKey
    private String name;
    private int voteStartAvailableThreshold = 4;
    private boolean allowVoteStart = true;
    private int voteStartThreshold = 2;
    private boolean sounds = true;
    private boolean keepPreviousGameSettings = true;
    private boolean allowVoteWeight = true;
    private int timerLength = 30; //Later Default will be 45
    private int maxGames = 10;
    private int minPlayers = 4;
    private int maxPlayers = 24;
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public int getMaxGames() {
        return maxGames;
    }
    
    public int getTimerLength() {
        return timerLength;
    }
    
    public boolean isVoteWeight() {
        return allowVoteWeight;
    }
    
    public boolean isKeepPreviousGameSettings() {
        return keepPreviousGameSettings;
    }
    
    public boolean isSounds() {
        return sounds;
    }
    
    public int getVoteStartThreshold() {
        return voteStartThreshold;
    }
    
    public boolean isAllowVoteStart() {
        return allowVoteStart;
    }
    
    public int getVoteStartAvailableThreshold() {
        return voteStartAvailableThreshold;
    }

    @Override
    public LobbySettings clone() {
        try {
            return (LobbySettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return new LobbySettings();
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
