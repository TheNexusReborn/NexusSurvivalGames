package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.stats.StatValue;

import java.util.*;

public class LobbyPlayer {
    private final NexusPlayer player;
    private boolean spectating = false, voteStart;
    private int mapVote = -1;
    
    public LobbyPlayer(NexusPlayer player) {
        this.player = player;
    }
    
    public void setSpectating(boolean spectating) {
        this.spectating = spectating;
    }
    
    public void setVoteStart(boolean voteStart) {
        this.voteStart = voteStart;
    }
    
    public void setMapVote(int mapVote) {
        this.mapVote = mapVote;
    }
    
    public NexusPlayer getPlayer() {
        return player;
    }
    
    public boolean isSpectating() {
        if (spectating) {
            return true;
        }
        
        return getToggleValue("vanish");
    }
    
    public boolean isVoteStart() {
        return voteStart;
    }
    
    public int getMapVote() {
        return mapVote;
    }
    
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }
    
    public Rank getRank() {
        return getPlayer().getRanks().get();
    }
    
    public boolean getToggleValue(String toggle) {
        return getPlayer().getToggles().getValue(toggle);
    }
    
    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }
    
    public String getName() {
        return getPlayer().getName();
    }
    
    public void setActionBar(IActionBar actionBar) {
        this.player.setActionBar(actionBar);
    }
    
    public StatValue getStatValue(String statName) {
        return getPlayer().getStats().getValue(statName);
    }
}
