package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;

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
    public void addScore(int i) {
        this.trueStats.addScore(i);
        super.addScore(i);
    }
    
    @Override
    public void addDeathmatchesReached(int i) {
        this.trueStats.addDeathmatchesReached(i);
        super.addDeathmatchesReached(i);
    }
    
    @Override
    public void addGames(int i) {
        this.trueStats.addGames(i);
        super.addGames(i);
    }
    
    @Override
    public void addWins(int i) {
        this.trueStats.addWins(i);
        super.addWins(i);
    }
    
    @Override
    public void addWinStreak(int i) {
        this.trueStats.addWinStreak(i);
        super.addWinStreak(i);
    }
    
    @Override
    public void addMutationPasses(int i) {
        this.trueStats.addMutationPasses(i);
        super.addMutationPasses(i);
    }
    
    @Override
    public void addDeaths(int i) {
        this.trueStats.addDeaths(i);
        super.addDeaths(i);
    }
    
    @Override
    public void addMutationDeaths(int i) {
        this.trueStats.addMutationDeaths(i);
        super.addMutationDeaths(i);
    }
    
    @Override
    public void addKills(int i) {
        this.trueStats.addKills(i);
        super.addKills(i);
    }
    
    @Override
    public void addMutationKills(int i) {
        this.trueStats.addMutationKills(i);
        super.addMutationKills(i);
    }
    
    @Override
    public void addAssists(int i) {
        this.trueStats.addAssists(i);
        super.addAssists(i);
    }
    
    @Override
    public void addChestsLooted(int i) {
        this.trueStats.addChestsLooted(i);
        super.addChestsLooted(i);
    }
    
    @Override
    public void addTimesMutated(int i) {
        this.trueStats.addTimesMutated(i);
        super.addTimesMutated(i);
    }
    
    @Override
    public void addSponsoredOthers(int i) {
        this.trueStats.addSponsoredOthers(i);
        super.addSponsoredOthers(i);
    }
    
    @Override
    public void addSponsorsReceived(int i) {
        this.trueStats.addSponsorsReceived(i);
        super.addSponsorsReceived(i);
    }
    
    @Override
    public UUID getUniqueId() {
        return super.getUniqueId();
    }
}
