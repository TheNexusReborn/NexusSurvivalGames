package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.storage.annotations.TableInfo;
import com.thenexusreborn.survivalgames.settings.collection.SettingList;
import com.thenexusreborn.survivalgames.settings.object.impl.LobbySetting;

@TableInfo("sglobbysettings")
public class LobbySettings extends SettingList<LobbySetting> {
    public LobbySettings() {
        super("lobby");
    }
    
    public LobbySettings(String type) {
        super(type);
    }
    
    public int getMaxPlayers() {
        return getValue("max_players").getAsInt();
    }
    
    public int getMinPlayers() {
        return getValue("min_players").getAsInt();
    }
    
    public int getMaxGames() {
        return getValue("max_games").getAsInt();
    }
    
    public int getTimerLength() {
        return getValue("timer_length").getAsInt();
    }
    
    public boolean isVoteWeight() {
        return getValue("allow_vote_weight").getAsBoolean();
    }
    
    public boolean isKeepPreviousGameSettings() {
        return getValue("keep_previous_game_settings").getAsBoolean();
    }
    
    public boolean isSounds() {
        return getValue("sounds").getAsBoolean();
    }
    
    public int getVoteStartThreshold() {
        return getValue("vote_start_threshold").getAsInt();
    }
    
    @Override
    public LobbySettings clone() {
        return (LobbySettings) super.clone();
    }
}
