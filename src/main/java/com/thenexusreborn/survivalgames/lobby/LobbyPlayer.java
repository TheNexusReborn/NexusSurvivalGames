package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.player.*;
import me.firestar311.starlib.api.Value;

import java.util.UUID;

public class LobbyPlayer {
    private final NexusPlayer player;
    private boolean spectating, voteStart;
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
        return getPlayer().getRank();
    }
    
    public boolean getToggleValue(String toggle) {
        return getPlayer().getToggleValue(toggle);
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
    
    public Value getStatValue(String statName) {
        return getPlayer().getStatValue(statName);
    }

    @Override
    public String toString() {
        return "LobbyPlayer{" +
                "player=" + player +
                ", spectating=" + spectating +
                ", voteStart=" + voteStart +
                ", mapVote=" + mapVote +
                '}';
    }
}
