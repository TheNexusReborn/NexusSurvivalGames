package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.gamemaps.MapManager;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.sql.SQLException;
import java.util.logging.Level;

public class SQLMapManager extends MapManager<SGMap> {
    public SQLMapManager(SurvivalGames plugin) {
        super(plugin);
    }

    public void loadMaps() {
        try {
            this.gameMaps.addAll(NexusReborn.getPrimaryDatabase().get(SGMap.class));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get the maps from the database", e);
            return;
        }

        //This runs the checks to see if a map has the minimum requirements for a map to work.
        for (SGMap gameMap : this.gameMaps) {
            boolean oldValue = gameMap.isActive();
            gameMap.setActive(oldValue);
            //Only save the map if the old value was true and the new one is false
            if (oldValue && !gameMap.isActive()) {
                NexusReborn.getPrimaryDatabase().saveSilent(gameMap); 
            }
        }
    }

    @Override
    public void deleteMap(SGMap map) {
        try {
            NexusReborn.getPrimaryDatabase().delete(map);
            this.getMaps().remove(map);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveMaps() {
        for (SGMap gameMap : this.gameMaps) {
            saveMap(gameMap);
        }
    }

    @Override
    public void saveMap(SGMap gameMap) {
        try {
            NexusReborn.getPrimaryDatabase().save(gameMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SGMap loadMap(String name) {
        return null;
    }

    @Deprecated
    public void saveToDatabase(SGMap gameMap) {
        saveMap(gameMap);
    }
}