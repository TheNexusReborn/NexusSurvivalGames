package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.sql.*;
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
        
        if (gameMaps.size() == 0) {
            List<GameMap> maps = new ArrayList<>(); 
            List<MapSpawn> spawns = new ArrayList<>();
            try (Connection connection = plugin.getNexusCore().getConnection("nexusmaps"); Statement statement = connection.createStatement()) {
                ResultSet mapSet = statement.executeQuery("select * from sgmaps;");
                while (mapSet.next()) {
                    int id = mapSet.getInt("id");
                    String name = mapSet.getString("name");
                    String url = mapSet.getString("url");
                    int centerX = mapSet.getInt("centerX");
                    int centerY = mapSet.getInt("centerY");
                    int centerZ = mapSet.getInt("centerZ");
                    int borderRadius = mapSet.getInt("borderRadius");
                    int dmBorderRadius = mapSet.getInt("dmBorderRadius");
                    String creators = mapSet.getString("creators");
                    boolean active = Boolean.parseBoolean(mapSet.getString("active"));
                    GameMap gameMap = new GameMap(url, name);
                    gameMap.setId(id);
                    gameMap.setCenter(new Position(centerX, centerY, centerZ));
                    gameMap.setBorderDistance(borderRadius);
                    gameMap.setDeathmatchBorderDistance(dmBorderRadius);
                    gameMap.getCreators().addAll(Arrays.asList(creators.split(",")));
                    gameMap.setActive(active);
                    maps.add(gameMap);
                }
                
                ResultSet spawnsSet = statement.executeQuery("select * from sgmapspawns;");
                while (spawnsSet.next()) {
                    int index = spawnsSet.getInt("id");
                    int mapId = spawnsSet.getInt("mapId");
                    int x = spawnsSet.getInt("x");
                    int y = spawnsSet.getInt("y");
                    int z = spawnsSet.getInt("z");
                    MapSpawn mapSpawn = new MapSpawn(mapId, index, x, y, z);
                    spawns.add(mapSpawn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            Iterator<MapSpawn> spawnIterator = spawns.iterator();
            while (spawnIterator.hasNext()) {
                MapSpawn spawn = spawnIterator.next();
                for (GameMap map : maps) {
                    if (map.getId() == spawn.getMapId()) {
                        map.getSpawns().add(spawn);
                        spawnIterator.remove();
                    }
                }
            }
            
            if (spawns.size() > 0) {
                plugin.getLogger().warning("There was a total of " + spawns.size() + " spawns that didn't have a map.");
            }
    
            for (GameMap map : maps) {
                map.setId(0);
            }
    
            for (GameMap map : maps) {
                plugin.getMapDatabase().saveSilent(map);
                this.gameMaps.add(map);
            }
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
        plugin.getMapDatabase().saveSilent(gameMap);
    }
    
    public void addMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }
    
    public List<GameMap> getMaps() {
        return this.gameMaps;
    }
}
