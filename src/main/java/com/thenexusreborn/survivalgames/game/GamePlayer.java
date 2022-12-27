package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.stats.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.entity.EntityType;

import java.util.*;

public class GamePlayer {
    private final NexusPlayer nexusPlayer;
    private GameTeam team;
    private boolean spectatorByDeath, newPersonalBestNotified = false;
    private TrackerInfo trackerInfo;
    private int kills, killStreak, assists;
    private boolean mutated;
    private Mutation mutation;
    private boolean deathByMutation;
    private Bounty bounty;
    private CombatTag combatTag;
    private DamageInfo damageInfo;
    private Map<Long, DeathInfo> deaths = new TreeMap<>();
    
    public GamePlayer(NexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
        this.bounty = new Bounty(nexusPlayer.getUniqueId());
        this.combatTag = new CombatTag(nexusPlayer.getUniqueId());
        this.damageInfo = new DamageInfo(nexusPlayer.getUniqueId());
    }

    public StatChange changeStat(String statName, Object value, StatOperator operator) {
        return getNexusPlayer().changeStat(statName, value, operator).push(); //TODO Change how this works eventually
    }

    public Value getStatValue(String statName) {
        return getNexusPlayer().getStatValue(statName);
    }

    public String getColoredName() {
        return getNexusPlayer().getColoredName();
    }

    public Rank getRank() {
        return getNexusPlayer().getRank();
    }

    public boolean getToggleValue(String toggle) {
        return getNexusPlayer().getToggleValue(toggle);
    }

    public NexusScoreboard getScoreboard() {
        return getNexusPlayer().getScoreboard();
    }

    public String getDisplayName() {
        return getNexusPlayer().getDisplayName();
    }

    public String getName() {
        return getNexusPlayer().getName();
    }

    public void removeCredits(int credits) {
        getNexusPlayer().removeCredits(credits);
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
    
    public void setSpectatorByDeath(boolean value) {
        this.spectatorByDeath = value;
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
    
    public int getTotalTimesMutated() {
        int totalMutations = 0;
        for (DeathInfo deathInfo : new ArrayList<>(deaths.values())) {
            if (deathInfo.getTeam() == GameTeam.MUTATIONS) {
                totalMutations++;
            }
        }
        return totalMutations;
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
        
        if (getTotalTimesMutated() >= game.getSettings().getMaxMutationAmount()) {
            return false;
        }
        
        return game.getSettings().isAllowMutations();
    }
    
    public void setCombat(GamePlayer other) {
        if (other.getUniqueId() == this.getUniqueId()) {
            return;
        }

        if (other.getTeam() == GameTeam.SPECTATORS) {
            return;
        }

        if (getTeam() == GameTeam.SPECTATORS) {
            return;
        }

        if (!getCombatTag().isInCombatWith(other.getUniqueId())) {
            sendMessage("&6&l>> &cYou are now in combat with " + other.getNexusPlayer().getColoredName() + "&c!");
        }
        getCombatTag().setOther(other.getUniqueId());
    }
    
    public void addDeathInfo(DeathInfo deathInfo) {
        this.deaths.put(deathInfo.getTime(), deathInfo);
    }
    
    public Map<Long, DeathInfo> getDeaths() {
        return deaths;
    }
    
    public int getAssists() {
        return this.assists;
    }
    
    public void setAssists(int amount) {
        this.assists = amount;
    }

    public boolean killedByPlayer() {
        for (DeathInfo death : this.deaths.values()) {
            if (death.getKiller() != null) {
                if (death.getKiller().getType() == EntityType.PLAYER) {
                    return true;
                }
            }
        }
        return false;
    }

    public UUID getKiller() {
        for (DeathInfo death : this.deaths.values()) {
            if (death.getKiller() != null) {
                if (death.getKiller().getType() == EntityType.PLAYER) {
                    return death.getKiller().getKiller();
                }
            }
        }
        return null;
    }
}
