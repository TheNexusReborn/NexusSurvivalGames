package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@TableName("sgplayerstats")
public class SGPlayerStats {
    
    private static final Map<String, Field> fields = new HashMap<>();

    static {
        for (Field field : SGPlayerStats.class.getDeclaredFields()) {
            field.setAccessible(true);
            fields.put(field.getName().toLowerCase(), field);
        }
    }
    
    @PrimaryKey
    protected UUID uniqueId;
    protected int score = 100, kills, highestKillstreak, games, wins, winStreak, deaths, deathmatchesReached, chestsLooted, assists, mutationKills;
    protected int mutationDeaths, mutationPasses, sponsoredOthers, sponsorsReceived, timesMutated, souls;
    
    protected SGPlayerStats() {}
    
    public SGPlayerStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public int getValue(String name) {
        try {
            return (int) fields.get(name.toLowerCase()).get(this);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getSouls() {
        return souls;
    }
    
    public void setSouls(int souls) {
        this.souls = souls;
    }
    
    public void setTimesMutated(int timesMutated) {
        this.timesMutated = Math.max(0, timesMutated);
    }

    public int getDeathmatchesReached() {
        return deathmatchesReached;
    }

    public void setDeathmatchesReached(int deathmatchesReached) {
        this.deathmatchesReached = Math.max(0, deathmatchesReached);
    }

    public void setScore(int score) {
        this.score = Math.max(10, score);
    }

    public void setKills(int kills) {
        this.kills = Math.max(0, kills);
    }

    public void setHighestKillstreak(int highestKillstreak) {
        this.highestKillstreak = Math.max(0, highestKillstreak);
    }

    public void setGames(int games) {
        this.games = Math.max(0, games);
    }

    public void setWins(int wins) {
        this.wins = Math.max(0, wins);
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = Math.max(0, winStreak);
    }

    public void setDeaths(int deaths) {
        this.deaths = Math.max(0, deaths);
    }

    public void setChestsLooted(int chestsLooted) {
        this.chestsLooted = Math.max(0, chestsLooted);
    }

    public void setAssists(int assists) {
        this.assists = Math.max(0, assists);
    }

    public void setMutationKills(int mutationKills) {
        this.mutationKills = Math.max(0, mutationKills);
    }

    public void setMutationDeaths(int mutationDeaths) {
        this.mutationDeaths = Math.max(0, mutationDeaths);
    }

    public void setMutationPasses(int mutationPasses) {
        this.mutationPasses = Math.max(0, mutationPasses);
    }

    public void setSponsoredOthers(int sponsoredOthers) {
        this.sponsoredOthers = Math.max(0, sponsoredOthers);
    }

    public void setSponsorsReceived(int sponsorsReceived) {
        this.sponsorsReceived = Math.max(0, sponsorsReceived);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public int getScore() {
        return score;
    }

    public int getKills() {
        return kills;
    }

    public int getHighestKillstreak() {
        return highestKillstreak;
    }

    public int getGames() {
        return games;
    }

    public int getWins() {
        return wins;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getChestsLooted() {
        return chestsLooted;
    }

    public int getAssists() {
        return assists;
    }

    public int getMutationKills() {
        return mutationKills;
    }

    public int getMutationDeaths() {
        return mutationDeaths;
    }

    public int getMutationPasses() {
        return mutationPasses;
    }

    public int getSponsoredOthers() {
        return sponsoredOthers;
    }

    public int getSponsorsReceived() {
        return sponsorsReceived;
    }
    
    public void addSouls(int i) {
        setSouls(souls + i);
    }

    public void addScore(int i) {
        setScore(score + i);
    }

    public void addDeathmatchesReached(int i) {
        setDeathmatchesReached(this.deathmatchesReached + i);
    }

    public void addGames(int i) {
        setGames(this.games + i);
    }

    public void addWins(int i) {
        setWins(wins + i);
    }

    public void addWinStreak(int i) {
        setWinStreak(winStreak + 1);
    }

    public void addMutationPasses(int i) {
        setMutationPasses(mutationPasses + 1);
    }

    public void addDeaths(int i) {
        setDeaths(deaths + 1);
    }

    public void addMutationDeaths(int i) {
        setMutationDeaths(mutationDeaths + 1);
    }

    public void addKills(int i) {
        setKills(kills + 1);
    }

    public void addMutationKills(int i) {
        setMutationKills(mutationKills + i);
    }

    public void addAssists(int i) {
        setAssists(assists + i);
    }

    public void addChestsLooted(int i) {
        setChestsLooted(chestsLooted + i);
    }

    public void addTimesMutated(int i) {
        setTimesMutated(timesMutated + i);
    }

    public void addSponsoredOthers(int i) {
        setSponsoredOthers(sponsoredOthers + i);
    }

    public void addSponsorsReceived(int i) {
        setSponsorsReceived(sponsorsReceived + i);
    }
    
    public static Map<String, Field> getFields() {
        return new HashMap<>(fields);
    }
}