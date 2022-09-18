package com.thenexusreborn.survivalgames.data.handler;

import com.thenexusreborn.api.data.objects.*;
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
        
        for (MapSpawn mapSpawn : gameMap.getSpawns()) {
            database.push(mapSpawn);
        }
    }
}
