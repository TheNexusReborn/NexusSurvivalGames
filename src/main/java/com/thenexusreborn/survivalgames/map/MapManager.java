package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class MapManager {
    private SurvivalGames plugin;
    
    private Config mapsConfig;
    
    private List<GameMap> gameMaps = new ArrayList<>();
    
    public MapManager(SurvivalGames plugin) {
        this.plugin = plugin;
        load();
    }
    
    public void load() {
        plugin.getLogger().info("Loading the Maps...");
        try (Connection connection = plugin.getMapConnection(); Statement mapStatement = connection.createStatement(); Statement spawnsStatement = connection.createStatement()) {
            ResultSet resultSet = mapStatement.executeQuery("select * from sgmaps;");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name").replace("''", "'");
                plugin.getLogger().info("Loading map " + name);
                String url = resultSet.getString("url");
                int centerX = resultSet.getInt("centerX");
                int centerY = resultSet.getInt("centerY");
                int centerZ = resultSet.getInt("centerZ");
                int borderRadius = resultSet.getInt("borderRadius");
                int dmBorderRadius = resultSet.getInt("dmBorderRadius");
                String rawCreators = resultSet.getString("creators");
                boolean active = Boolean.parseBoolean(resultSet.getString("active"));
                
                GameMap gameMap = new GameMap(url, name);
                gameMap.setId(id);
                gameMap.setCenter(new Position(centerX, centerY, centerZ));
                gameMap.setBorderDistance(borderRadius);
                gameMap.setDeathmatchBorderDistance(dmBorderRadius);
                gameMap.setActive(active);
                for (String creator : rawCreators.split(",")) {
                    gameMap.addCreator(creator);
                }
                
                ResultSet spawnResultSet = spawnsStatement.executeQuery("select * from sgmapspawns where mapId='" + gameMap.getId() + "';");
                while (spawnResultSet.next()) {
                    int spawnId = spawnResultSet.getInt("id");
                    int x = spawnResultSet.getInt("x");
                    int y = spawnResultSet.getInt("y");
                    int z = spawnResultSet.getInt("z");
                    Position spawn = new Position(x, y, z);
                    gameMap.setSpawn(spawnId, spawn);
                }
                this.gameMaps.add(gameMap);
            }
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
        try (Connection connection = plugin.getMapConnection()) {
            String mapSql;
            
            if (gameMap.getId() > 0) {
                //language=MySQL
                mapSql = "update sgmaps set name=?, url=?, centerX=?, centerY=?, centerZ=?, borderRadius=?, dmBorderRadius=?, creators=?, active=? where id='" + gameMap.getId() + "';";
            } else {
                //language=MySQL
                mapSql = "insert into sgmaps(name, url, centerX, centerY, centerZ, borderRadius, dmBorderRadius, creators, active) values(?, ?, ?, ?, ?, ?, ?, ?, ?);";
            }
            
            try (PreparedStatement mapStatement = connection.prepareStatement(mapSql, Statement.RETURN_GENERATED_KEYS); PreparedStatement spawnUpdateStatement = connection.prepareStatement("update sgmapspawns set x=?, y=?, z=? where id=? and mapId=?;"); PreparedStatement spawnInsertStatement = connection.prepareStatement("insert into sgmapspawns(id, mapId, x, y, z) values(?, ?, ?, ?, ?);")) {
                mapStatement.setString(1, gameMap.getName().replace("'", "''"));
                mapStatement.setString(2, gameMap.getUrl());
                mapStatement.setInt(3, gameMap.getCenter().getX());
                mapStatement.setInt(4, gameMap.getCenter().getY());
                mapStatement.setInt(5, gameMap.getCenter().getZ());
                mapStatement.setInt(6, gameMap.getBorderDistance());
                mapStatement.setInt(7, gameMap.getDeathmatchBorderDistance());
                StringBuilder creators = new StringBuilder();
                for (String creator : gameMap.getCreators()) {
                    if (creator != null && !creator.equals("") && !creator.equals(" ")) {
                        creators.append(creator).append(",");
                    }
                }
                if (creators.length() == 0) {
                    creators.append(" ");
                }
                mapStatement.setString(8, creators.substring(0, creators.length() - 1));
                mapStatement.setString(9, gameMap.isActive() + "");
                mapStatement.executeUpdate();
                if (mapSql.contains("insert into")) {
                    ResultSet generatedKeys = mapStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        gameMap.setId(generatedKeys.getInt(1));
                    }
                }
                
                for (Entry<Integer, Position> entry : gameMap.getSpawns().entrySet()) {
                    try (Statement spawnQuery = connection.createStatement()) {
                        ResultSet resultSet = spawnQuery.executeQuery("select * from nexusmaps.sgmapspawns where id='" + entry.getKey() + "' and mapId='" + gameMap.getId() + "';");
                        if (resultSet.next()) {
                            spawnUpdateStatement.setInt(1, entry.getValue().getX());
                            spawnUpdateStatement.setInt(2, entry.getValue().getY());
                            spawnUpdateStatement.setInt(3, entry.getValue().getZ());
                            spawnUpdateStatement.setInt(4, entry.getKey());
                            spawnUpdateStatement.setInt(5, gameMap.getId());
                            spawnUpdateStatement.addBatch();
                        } else {
                            spawnInsertStatement.setInt(1, entry.getKey());
                            spawnInsertStatement.setInt(2, gameMap.getId());
                            spawnInsertStatement.setInt(3, entry.getValue().getX());
                            spawnInsertStatement.setInt(4, entry.getValue().getY());
                            spawnInsertStatement.setInt(5, entry.getValue().getZ());
                            spawnInsertStatement.addBatch();
                        }
                    }
                }
                spawnUpdateStatement.executeBatch();
                spawnInsertStatement.executeBatch();
            }
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
