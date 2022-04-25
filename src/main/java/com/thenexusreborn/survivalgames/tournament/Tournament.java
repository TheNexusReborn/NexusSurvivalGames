package com.thenexusreborn.survivalgames.tournament;

import java.util.*;

public class Tournament {
    private UUID host;
    private String name;
    private boolean active;
    private Set<UUID> participants = new HashSet<>(), spectators = new HashSet<>();
    private int pointsPerKill, pointsPerWin, pointsPerSurvival;
    private Map<UUID, Integer> scores = new HashMap<>();
    
    public Tournament(UUID host, String name) {
        this.host = host;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<UUID, Integer> getScores() {
        return scores;
    }
    
    public void incrementScore(UUID uuid, int score) {
        if (this.scores.containsKey(uuid)) {
            this.scores.put(uuid, this.scores.get(uuid) + score);
        } else {
            this.scores.put(uuid, score);
        }
    }
    
    public UUID getHost() {
        return host;
    }
    
    public void setHost(UUID host) {
        this.host = host;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Set<UUID> getParticipants() {
        return participants;
    }
    
    public void setParticipants(Set<UUID> participants) {
        this.participants = participants;
    }
    
    public Set<UUID> getSpectators() {
        return spectators;
    }
    
    public void setSpectators(Set<UUID> spectators) {
        this.spectators = spectators;
    }
    
    public int getPointsPerKill() {
        return pointsPerKill;
    }
    
    public void setPointsPerKill(int pointsPerKill) {
        this.pointsPerKill = pointsPerKill;
    }
    
    public int getPointsPerWin() {
        return pointsPerWin;
    }
    
    public void setPointsPerWin(int pointsPerWin) {
        this.pointsPerWin = pointsPerWin;
    }
    
    public int getPointsPerSurvival() {
        return pointsPerSurvival;
    }
    
    public void setPointsPerSurvival(int pointsPerSurvival) {
        this.pointsPerSurvival = pointsPerSurvival;
    }
}
