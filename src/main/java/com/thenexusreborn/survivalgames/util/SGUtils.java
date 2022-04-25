package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.nexuscore.util.command.CommandActor;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.util.Vector;

public class SGUtils {
    
    private static SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    public static GameMap getLoadedGameMap(String input, CommandActor actor) {
        GameMap gameMap = getGameMapFromInput(input, actor);
        if (gameMap == null) {
            return null;
        }
        
        if (gameMap.getWorld() == null) {
            actor.sendMessage("&cThat map is not loaded, please load before teleporting.");
            return null;
        }
        return gameMap;
    }
    
    public static GameMap getGameMapFromInput(String input, CommandActor actor) {
        GameMap gameMap = null;
        for (GameMap map : plugin.getMapManager().getMaps()) {
            if (map.getName().toLowerCase().replace(" ", "_").replace("'", "").equalsIgnoreCase(input) || map.getFileName().equalsIgnoreCase(input)) {
                gameMap = map;
            }
        }
        
        if (gameMap == null) {
            actor.sendMessage("&cCould not find a map with that name or file name.");
        }
        
        return gameMap;
    }
    
    public static float getAngle(Vector point1, Vector point2) {
        double dx = point2.getX() - point1.getX();
        double dz = point2.getZ() - point1.getZ();
        float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        if (angle < 0) {
            angle += 360.0F;
        }
        return angle;
    }
}
