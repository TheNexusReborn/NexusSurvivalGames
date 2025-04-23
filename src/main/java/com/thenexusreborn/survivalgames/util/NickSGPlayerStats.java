package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.util.UUID;

@TableName("nicksgplayerstats")
public class NickSGPlayerStats extends SGPlayerStats {
    
    private boolean persist;
    
    @ColumnIgnored
    private SGPlayerStats trueStats;
    
    public NickSGPlayerStats(UUID uniqueId, SGPlayerStats trueStats, boolean persist) {
        super(uniqueId);
        this.trueStats = trueStats;
        this.persist = persist;
    }
    
    protected NickSGPlayerStats() {}
    
    public SGPlayerStats getTrueStats() {
        if (this.trueStats == null) {
            this.trueStats = SurvivalGames.getInstance().getPlayerRegistry().get(this.uniqueId).getTrueStats();
        }
        
        return trueStats;
    }
    
    public boolean isPersist() {
        return persist;
    }
    
    public void setPersist(boolean persist) {
        this.persist = persist;
    }
    
    public void setTrueStats(SGPlayerStats trueStats) {
        this.trueStats = trueStats;
    }
    
    @Override
    public void setWinStreak(int winStreak) {
        getTrueStats().setWinStreak(winStreak);
        super.setWinStreak(winStreak);
    }
    
    @Override
    public void addScore(int i) {
        getTrueStats().addScore(i);
        super.addScore(i);
    }
    
    @Override
    public void addDeathmatchesReached(int i) {
        getTrueStats().addDeathmatchesReached(i);
        super.addDeathmatchesReached(i);
    }
    
    @Override
    public void addGames(int i) {
        getTrueStats().addGames(i);
        super.addGames(i);
    }
    
    @Override
    public void addWins(int i) {
        getTrueStats().addWins(i);
        super.addWins(i);
    }
    
    @Override
    public void addWinStreak(int i) {
        getTrueStats().addWinStreak(i);
        super.addWinStreak(i);
    }
    
    @Override
    public void addMutationPasses(int i) {
        getTrueStats().addMutationPasses(i);
        super.addMutationPasses(i);
    }
    
    @Override
    public void addDeaths(int i) {
        getTrueStats().addDeaths(i);
        super.addDeaths(i);
    }
    
    @Override
    public void addMutationDeaths(int i) {
        getTrueStats().addMutationDeaths(i);
        super.addMutationDeaths(i);
    }
    
    @Override
    public void addKills(int i) {
        getTrueStats().addKills(i);
        super.addKills(i);
    }
    
    @Override
    public void addMutationKills(int i) {
        getTrueStats().addMutationKills(i);
        super.addMutationKills(i);
    }
    
    @Override
    public void addAssists(int i) {
        getTrueStats().addAssists(i);
        super.addAssists(i);
    }
    
    @Override
    public void addChestsLooted(int i) {
        getTrueStats().addChestsLooted(i);
        super.addChestsLooted(i);
    }
    
    @Override
    public void addTimesMutated(int i) {
        getTrueStats().addTimesMutated(i);
        super.addTimesMutated(i);
    }
    
    @Override
    public void addSponsoredOthers(int i) {
        getTrueStats().addSponsoredOthers(i);
        super.addSponsoredOthers(i);
    }
    
    @Override
    public void addSponsorsReceived(int i) {
        getTrueStats().addSponsorsReceived(i);
        super.addSponsorsReceived(i);
    }
    
    @Override
    public UUID getUniqueId() {
        return super.getUniqueId();
    }
}
