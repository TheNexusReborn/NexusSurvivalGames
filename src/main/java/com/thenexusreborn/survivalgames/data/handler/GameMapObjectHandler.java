package com.thenexusreborn.survivalgames.data.handler;

import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.sql.objects.ObjectHandler;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.sql.objects.Table;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.MapSpawn;
import com.thenexusreborn.gamemaps.model.SGMap;

import java.sql.SQLException;
import java.util.List;

public class GameMapObjectHandler extends ObjectHandler {
    public GameMapObjectHandler(Object object, SQLDatabase database, Table table) {
        super(object, database, table);
    }
    
    @Override
    public void afterLoad() {
        SGMap gameMap = (SGMap) object;
        try {
            List<MapSpawn> mapSpawns = database.get(MapSpawn.class, "mapId", gameMap.getId());
            gameMap.setSpawns(mapSpawns);
            gameMap.recalculateSpawns();
            
            List<MapRating> ratings = NexusReborn.getPrimaryDatabase().get(MapRating.class, "mapName", gameMap.getName().toLowerCase().replace("'", "''"));
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
                NexusReborn.getPrimaryDatabase().save(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        database.flush();
    }
}
