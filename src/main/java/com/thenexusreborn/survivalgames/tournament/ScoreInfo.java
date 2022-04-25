package com.thenexusreborn.survivalgames.tournament;

import java.util.*;

public class ScoreInfo implements Comparable<ScoreInfo> {
    private final UUID uuid;
    private int score;
    
    public ScoreInfo(UUID uuid, int score) {
        this.uuid = uuid;
        this.score = score;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScoreInfo scoreInfo = (ScoreInfo) o;
        return Objects.equals(uuid, scoreInfo.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    @Override
    public int compareTo(ScoreInfo o) {
        return Integer.compare(o.score, this.score);
    }
}
