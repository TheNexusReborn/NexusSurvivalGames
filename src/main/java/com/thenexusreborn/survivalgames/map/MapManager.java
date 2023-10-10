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
        try {
            this.gameMaps.addAll(NexusAPI.getApi().getPrimaryDatabase().get(GameMap.class));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get the maps from the database", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        
        plugin.getLogger().info("Total Maps: " + gameMaps.size());
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
        try {
            NexusAPI.getApi().getPrimaryDatabase().save(gameMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }

    public List<GameMap> getMaps() {
        return this.gameMaps;
    }
}
