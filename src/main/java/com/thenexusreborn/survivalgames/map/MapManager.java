package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.nexuscore.util.Config;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.sql.SQLException;
import java.util.*;

public class MapManager {
    private final SurvivalGames plugin;
    
    private Config mapsConfig;
    
    private final List<GameMap> gameMaps = new ArrayList<>();
    
    public MapManager(SurvivalGames plugin) {
        this.plugin = plugin;
        load();
    }
    
    public void load() {
        plugin.getLogger().info("Loading the Maps...");
        try {
            gameMaps.addAll(plugin.getMapDatabase().get(GameMap.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public GameMap getMap(String mapName) {
        for (GameMap gameMap : this.gameMaps) {
            if (gameMap.getName().equalsIgnoreCase(mapName)) {
                return gameMap;
            }
            
            if (gameMap.getName().replace(" ", "_").replace("'", "").equalsIgnoreCase(mapName)) {
                return gameMap;
            }
            
            if (gameMap.getUrl().equalsIgnoreCase(mapName)) {
                return gameMap;
            }
        }
        return null;
    }
    
    public void saveToDatabase(GameMap gameMap) {
        plugin.getMapDatabase().push(gameMap);
    }
    
    public void addMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }
    
    public List<GameMap> getMaps() {
        return this.gameMaps;
    }
}
