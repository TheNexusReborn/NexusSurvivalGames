package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Sound;

import java.util.UUID;

public class LobbyPlayer {
    private final SGPlayer player;
    private boolean spectating, voteStart;
    private int mapVote = -1;
    
    public LobbyPlayer(SGPlayer player, SGPlayerStats stats) {
        this.player = player;
    }

    public SGPlayerStats getStats() {
        return player.getStats();
    }
    
    public SGPlayerStats getTrueStats() {
        return player.getTrueStats();
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
        return player.getNexusPlayer();
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
    
    public void playSound(Sound sound) {
        if (!player.getLobby().getLobbySettings().isSounds()) {
            return;
        }
        
        this.player.playSound(sound);
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
        this.player.getNexusPlayer().setActionBar(actionBar);
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
    
    public Rank getEffectiveRank() {
        return this.player.getNexusPlayer().getEffectiveRank();
    }
    
    public String getTrueName() {
        return this.player.getTrueName();
    }
    
    public boolean isNicked() {
        return this.player.getNexusPlayer().isNicked();
    }
}
