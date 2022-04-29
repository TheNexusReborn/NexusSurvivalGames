package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;

import java.util.UUID;

public class GamePlayer {
    private SpigotNexusPlayer nexusPlayer;
    private GameTeam team;
    private DeathInfo deathInfo;
    private boolean spectatorByDeath;
    private TrackerInfo trackerInfo;
    
    public GamePlayer(SpigotNexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
    }
    
    public SpigotNexusPlayer getNexusPlayer() {
        return nexusPlayer;
    }
    
    public void sendMessage(String message) {
        nexusPlayer.sendMessage(message);
    }
    
    public TrackerInfo getTrackerInfo() {
        return trackerInfo;
    }
    
    public void setTrackerInfo(TrackerInfo trackerInfo) {
        this.trackerInfo = trackerInfo;
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
