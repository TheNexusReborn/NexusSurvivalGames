package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.nexuscore.player.NexusPlayer;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;

import java.util.UUID;

public class GamePlayer {
    private NexusPlayer nexusPlayer;
    private GameTeam team;
    private DeathInfo deathInfo;
    private boolean spectatorByDeath;
    
    public GamePlayer(NexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
    }
    
    public NexusPlayer getNexusPlayer() {
        return nexusPlayer;
    }
    
    public void sendMessage(String message) {
        nexusPlayer.sendMessage(message);
    }
    
    public void setTeam(GameTeam team) {
        this.team = team;
    }
    
    public GameTeam getTeam() {
        return team;
    }
    
    public UUID getUniqueId() {
        return nexusPlayer.getUniqueId();
    }
    
    public void setDeathInfo(DeathInfo deathInfo) {
        this.deathInfo = deathInfo;
    }
    
    public void setSpectatorByDeath(boolean value) {
        this.spectatorByDeath = value;
    }
}
