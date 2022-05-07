package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.nexuscore.util.Config;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class MapManager {
    private SurvivalGames plugin;
    
    private Config mapsConfig;
    
    private List<GameMap> gameMaps = new ArrayList<>();
    
    public MapManager(SurvivalGames plugin) {
        this.plugin = plugin;
        
        this.mapsConfig = new Config(plugin, "gamemaps.yml");
        this.mapsConfig.setup();
        load();
    }
    
    public void save() {
        for (GameMap gameMap : this.gameMaps) {
            String mapId = gameMap.getName().toLowerCase().replace(" ", "_").replace("'", "");
            mapsConfig.set("maps." + mapId + ".filename", gameMap.getFileName());
            mapsConfig.set("maps." + mapId + ".name", gameMap.getName());
            try {
                gameMap.saveSettings();
            }  catch (Exception e) {}
        }
        mapsConfig.save();
    }
    
    protected void load() {
        ConfigurationSection mapsSection = mapsConfig.getConfigurationSection("maps");
        if (mapsSection != null) {
            for (String mapId : mapsSection.getKeys(false)) {
                String fileName = mapsSection.getString(mapId + ".filename");
                String name = mapsSection.getString(mapId + ".name");
                GameMap gameMap = new GameMap(fileName, name);
                this.gameMaps.add(gameMap);
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
            
            if (gameMap.getFileName().equalsIgnoreCase(mapName)) {
                return gameMap;
            }
        }
        return null;
    }
    
    public void addMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }
    
    public List<GameMap> getMaps() {
        return this.gameMaps;
    }
}
