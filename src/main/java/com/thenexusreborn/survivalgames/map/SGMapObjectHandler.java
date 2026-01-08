package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.api.sql.objects.*;

import java.sql.SQLException;
import java.util.List;

public class SGMapObjectHandler extends ObjectHandler {
    public SGMapObjectHandler(Object object, SQLDatabase database, Table table) {
        super(object, database, table);
    }
    
    @Override
    public void afterLoad() {
        SGMap gameMap = (SGMap) object;
        try {
            List<MapSpawn> mapSpawns = database.get(MapSpawn.class, "mapId", gameMap.getId());
            gameMap.setSpawns(mapSpawns);
            gameMap.recalculateSpawns();
            
            List<MapRating> ratings = database.get(MapRating.class, "mapName", gameMap.getName().toLowerCase().replace("'", "''"));
            if (ratings != null) {
                gameMap.setRatings(ratings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void afterSave() {
        SGMap gameMap = (SGMap) object;
    
        for (MapSpawn spawn : gameMap.getSpawns()) {
            if (spawn.getMapId() != gameMap.getId()) {
                spawn.setMapId(gameMap.getId());
            }
        }
        
        try {
            for (MapSpawn mapSpawn : gameMap.getSpawns()) {
                database.save(mapSpawn);
            }
    
            for (MapRating rating : gameMap.getRatings().values()) {
                database.save(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        database.flush();
    }
}
