package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

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
        List<GameMap> maps;
        try {
            maps = NexusAPI.getApi().getPrimaryDatabase().get(GameMap.class);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get the maps from the database", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        
        plugin.getLogger().info("Total Maps: " + maps.size());

        List<MapSpawn> spawns;
        try {
            spawns = NexusAPI.getApi().getPrimaryDatabase().get(MapSpawn.class);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get the map spawns from the database", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
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

        if (!spawns.isEmpty()) {
            plugin.getLogger().warning("There was a total of " + spawns.size() + " spawns that didn't have a map.");
        }

        for (GameMap map : maps) {
            NexusAPI.getApi().getPrimaryDatabase().saveSilent(map);
            this.gameMaps.add(map);
        }
        plugin.getLogger().info("Processed Maps: " + this.gameMaps.size());
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
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(gameMap);
    }

    public void addMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }

    public List<GameMap> getMaps() {
        return this.gameMaps;
    }
}
