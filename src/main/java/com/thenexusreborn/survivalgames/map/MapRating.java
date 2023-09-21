package com.thenexusreborn.survivalgames.map;

import me.firestar311.starsql.api.annotations.table.TableName;

import java.util.UUID;

@TableName("sgmapratings")
public class MapRating {
    private long id;
    private String mapName;
    private UUID player;
    private int rating; //Min of 1 and max of 5
    private long timestamp; //For the cooldown and maybe other things
    
    private MapRating() {}
    
    public MapRating(String mapName, UUID player, int rating, long timestamp) {
        this.mapName = mapName;
        this.player = player;
        this.rating = rating;
        this.timestamp = timestamp;
    }
    
    public long getId() {
        return id;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public int getRating() {
        return rating;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
        setTimestamp(System.currentTimeMillis());
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}