package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("nicksgplayerstats")
public class NickSGPlayerStats extends SGPlayerStats {
    
    @ColumnIgnored
    private SGPlayerStats trueStats;
    
    public NickSGPlayerStats(UUID uniqueId, SGPlayerStats trueStats) {
        super(uniqueId);
        this.trueStats = trueStats;
    }
    
    protected NickSGPlayerStats() {}
    
    public SGPlayerStats getTrueStats() {
        return trueStats;
    }
    
    public void setTrueStats(SGPlayerStats trueStats) {
        this.trueStats = trueStats;
    }
    
    @Override
    public void setTimesMutated(int timesMutated) {
        trueStats.setTimesMutated(timesMutated);
        super.setTimesMutated(timesMutated);
    }
    
    @Override
    public void setDeathmatchesReached(int deathmatchesReached) {
        trueStats.setDeathmatchesReached(deathmatchesReached);
        super.setDeathmatchesReached(deathmatchesReached);
    }
    
    @Override
    public void setScore(int score) {
        trueStats.setScore(score);
        super.setScore(score);
    }
    
    @Override
    public void setKills(int kills) {
        trueStats.setKills(kills);
        super.setKills(kills);
    }
    
    @Override
    public void setHighestKillstreak(int highestKillstreak) {
        trueStats.setHighestKillstreak(highestKillstreak);
        super.setHighestKillstreak(highestKillstreak);
    }
    
    @Override
    public void setGames(int games) {
        trueStats.setGames(games);
        super.setGames(games);
    }
    
    @Override
    public void setWins(int wins) {
        trueStats.setWins(wins);
        super.setWins(wins);
    }
    
    @Override
    public void setWinStreak(int winStreak) {
        trueStats.setWinStreak(winStreak);
        super.setWinStreak(winStreak);
    }
    
    @Override
    public void setDeaths(int deaths) {
        trueStats.setDeaths(deaths);
        super.setDeaths(deaths);
    }
    
    @Override
    public void setChestsLooted(int chestsLooted) {
        trueStats.setChestsLooted(chestsLooted);
        super.setChestsLooted(chestsLooted);
    }
    
    @Override
    public void setAssists(int assists) {
        trueStats.setAssists(assists);
        super.setAssists(assists);
    }
    
    @Override
    public void setMutationKills(int mutationKills) {
        trueStats.setMutationKills(mutationKills);
        super.setMutationKills(mutationKills);
    }
    
    @Override
    public void setMutationDeaths(int mutationDeaths) {
        trueStats.setMutationDeaths(mutationDeaths);
        super.setMutationDeaths(mutationDeaths);
    }
    
    @Override
    public void setMutationPasses(int mutationPasses) {
        trueStats.setMutationPasses(mutationPasses);
        super.setMutationPasses(mutationPasses);
    }
    
    @Override
    public void setSponsoredOthers(int sponsoredOthers) {
        trueStats.setSponsoredOthers(sponsoredOthers);
        super.setSponsoredOthers(sponsoredOthers);
    }
    
    @Override
    public void setSponsorsReceived(int sponsorsReceived) {
        trueStats.setSponsorsReceived(sponsorsReceived);
        super.setSponsorsReceived(sponsorsReceived);
    }
    
    @Override
    public UUID getUniqueId() {
        return super.getUniqueId();
    }
}
