package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.mutations.Mutation;

import java.util.UUID;

public class GamePlayer {
    private final NexusPlayer nexusPlayer;
    private GameTeam team;
    private DeathInfo deathInfo;
    private boolean spectatorByDeath, newPersonalBestNotified = false;
    private TrackerInfo trackerInfo;
    private int kills, killStreak;
    private boolean mutated;
    private Mutation mutation;
    private boolean deathByMutation;
    
    public GamePlayer(NexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
    }
    
    public NexusPlayer getNexusPlayer() {
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
    
    public DeathInfo getDeathInfo() {
        return deathInfo;
    }
    
    public boolean isSpectatorByDeath() {
        return spectatorByDeath;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getKillStreak() {
        return killStreak;
    }
    
    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }
    
    public boolean isNewPersonalBestNotified() {
        return newPersonalBestNotified;
    }
    
    public void setNewPersonalBestNotified(boolean newPersonalBestNotified) {
        this.newPersonalBestNotified = newPersonalBestNotified;
    }
    
    public boolean hasMutated() {
        return mutated;
    }
    
    public void setMutated(boolean mutated) {
        this.mutated = mutated;
    }
    
    public void setMutation(Mutation mutation) {
        this.mutation = mutation;
    }
    
    public Mutation getMutation() {
        return mutation;
    }
    
    public void setDeathByMutation(boolean value) {
        this.deathByMutation = value;
    }
    
    public boolean deathByMutation() {
        return deathByMutation;
    }
}
