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
    private UUID uniqueId;
    private int score = 100, kills, highestKillstreak, games, wins, winStreak, deaths, deathmatchesReached, chestsLooted, assists, mutationKills;
    private int mutationDeaths, mutationPasses, sponsoredOthers, sponsorsReceived, timesMutated;

    private SGPlayerStats() {}
    
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

    public void setTimesMutated(int timesMutated) {
        this.timesMutated = timesMutated;
    }

    public int getDeathmatchesReached() {
        return deathmatchesReached;
    }

    public void setDeathmatchesReached(int deathmatchesReached) {
        this.deathmatchesReached = deathmatchesReached;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setHighestKillstreak(int highestKillstreak) {
        this.highestKillstreak = highestKillstreak;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setChestsLooted(int chestsLooted) {
        this.chestsLooted = chestsLooted;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void setMutationKills(int mutationKills) {
        this.mutationKills = mutationKills;
    }

    public void setMutationDeaths(int mutationDeaths) {
        this.mutationDeaths = mutationDeaths;
    }

    public void setMutationPasses(int mutationPasses) {
        this.mutationPasses = mutationPasses;
    }

    public void setSponsoredOthers(int sponsoredOthers) {
        this.sponsoredOthers = sponsoredOthers;
    }

    public void setSponsorsReceived(int sponsorsReceived) {
        this.sponsorsReceived = sponsorsReceived;
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

    public void addScore(int i) {
        this.score += i;
    }

    public void addDeathmatchesReached(int i) {
        this.deathmatchesReached += i;
    }

    public void addGames(int i) {
        this.games += i;
    }

    public void addWins(int i) {
        this.wins += i;
    }

    public void addWinStreak(int i) {
        this.winStreak += i;
    }

    public void addMutationPasses(int i) {
        this.mutationPasses += i;
    }

    public void addDeaths(int i) {
        this.deaths += i;
    }

    public void addMutationDeaths(int i) {
        this.mutationDeaths += i;
    }

    public void addKills(int i) {
        this.kills += i;
    }

    public void addMutationKills(int i) {
        this.mutationKills += i;
    }

    public void addAssists(int i) {
        this.assists += i;
    }

    public void addChestsLooted(int i) {
        this.chestsLooted += i;
    }

    public void addTimesMutated(int i) {
        this.timesMutated += i;
    }

    public void addSponsoredOthers(int i) {
        this.sponsoredOthers += i;
    }

    public void addSponsorsReceived(int i) {
        this.sponsorsReceived += i;
    }
    
    public static Map<String, Field> getFields() {
        return new HashMap<>(fields);
    }
}