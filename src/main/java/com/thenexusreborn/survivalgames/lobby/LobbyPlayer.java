package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;

import java.util.UUID;

public class LobbyPlayer {
    private final NexusPlayer player;
    private SGPlayerStats stats;
    private boolean spectating, voteStart;
    private int mapVote = -1;
    
    public LobbyPlayer(NexusPlayer player, SGPlayerStats stats) {
        this.player = player;
        this.stats = stats;
    }

    public SGPlayerStats getStats() {
        return stats;
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
