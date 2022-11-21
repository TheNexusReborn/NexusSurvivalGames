package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.deathold.DeathInfo;
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
    private Bounty bounty;
    private CombatTag combatTag;
    private DamageInfo damageInfo;
    
    public GamePlayer(NexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
        this.bounty = new Bounty(nexusPlayer.getUniqueId());
        this.combatTag = new CombatTag(nexusPlayer.getUniqueId());
        this.damageInfo = new DamageInfo(nexusPlayer.getUniqueId());
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
    
    public Bounty getBounty() {
        return bounty;
    }
    
    public CombatTag getCombatTag() {
        return combatTag;
    }
    
    public DamageInfo getDamageInfo() {
        return damageInfo;
    }
    
    public boolean canMutate() {
        if (deathByMutation) {
            return false;
        }
        
        if (mutated) {
            return false;
        }
        
        Game game = SurvivalGames.getPlugin(SurvivalGames.class).getGame();
        if (game == null) {
            return false;
        }
        
        if (!(game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH)) {
            return false;
        }
        
        return game.getSettings().isAllowMutations();
    }
    
    public void setCombat(NexusPlayer other) {
        if (!getCombatTag().isInCombatWith(other.getUniqueId())) {
            sendMessage("&6&l>> &cYou are now in combat with " + other.getColoredName() + "&c!");
        }
        getCombatTag().setOther(other.getUniqueId());
    }
}
