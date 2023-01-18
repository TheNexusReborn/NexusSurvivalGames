package com.thenexusreborn.survivalgames.data.handler;

import com.starmediadev.starsql.objects.*;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.survivalgames.map.*;

import java.sql.SQLException;
import java.util.List;

public class GameMapObjectHandler extends ObjectHandler {
    public GameMapObjectHandler(Object object, Database database, Table table) {
        super(object, database, table);
    }
    
    @Override
    public void afterLoad() {
        GameMap gameMap = (GameMap) object;
        try {
            List<MapSpawn> mapSpawns = database.get(MapSpawn.class, "mapId", gameMap.getId());
            gameMap.setSpawns(mapSpawns);
            gameMap.recalculateSpawns();
            
            List<MapRating> ratings = NexusAPI.getApi().getPrimaryDatabase().get(MapRating.class, "mapName", gameMap.getName().toLowerCase().replace("'", "''"));
            if (ratings != null) {
                gameMap.setRatings(ratings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void afterSave() {
        GameMap gameMap = (GameMap) object;
    
        for (MapSpawn spawn : gameMap.getSpawns()) {
            if (spawn.getMapId() != gameMap.getId()) {
                spawn.setMapId(gameMap.getId());
            }
        }
        
        try {
            for (MapSpawn mapSpawn : gameMap.getSpawns()) {
                database.push(mapSpawn);
            }
    
            for (MapRating rating : gameMap.getRatings().values()) {
                NexusAPI.getApi().getPrimaryDatabase().push(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        database.flush();
    }
}
