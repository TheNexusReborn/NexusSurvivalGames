package com.thenexusreborn.survivalgames.map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.*;

public class MapRating implements ConfigurationSerializable {
    
    static {
        ConfigurationSerialization.registerClass(MapRating.class);
    }
    
    private long id;
    private String mapName;
    private UUID player;
    private int rating;
    private long timestamp;
    
    private MapRating() {}
    
    public MapRating(String mapName, UUID player, int rating, long timestamp) {
        this.mapName = mapName;
        this.player = player;
        this.rating = rating;
        this.timestamp = timestamp;
    }
    
    public MapRating(Map<String, Object> serialized) {
        this.id = (long) ((int) serialized.get("id"));
        this.mapName = (String) serialized.get("mapName");
        this.player = UUID.fromString((String) serialized.get("player"));
        this.rating = (int) serialized.get("rating");
        this.timestamp = (long) ((int) serialized.get("timestamp"));
    }

    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(Map.of("id", this.id, "mapName", this.mapName, "player", this.player.toString(), "rating", this.rating, "timestamp", this.timestamp));
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