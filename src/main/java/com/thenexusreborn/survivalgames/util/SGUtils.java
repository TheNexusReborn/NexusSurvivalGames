package com.thenexusreborn.survivalgames.util;

import com.google.common.io.*;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SGUtils {
    
    private static SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    public static void sendToHub(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("H1");
        player.sendPluginMessage(plugin.getNexusCore(), "BungeeCord", out.toByteArray());
    }
    
    public static String getMapNameFromCommand(String[] args, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }
    
    public static GameMap getLoadedGameMap(String input, CommandSender actor) {
        GameMap gameMap = getGameMapFromInput(input, actor);
        if (gameMap == null) {
            return null;
        }
        
        if (gameMap.getWorld() == null) {
            actor.sendMessage(MCUtils.color(MsgType.WARN + "That map is not loaded, please load before teleporting."));
            return null;
        }
        return gameMap;
    }
    
    public static GameMap getGameMapFromInput(String input, CommandSender actor) {
        GameMap gameMap = null;
        for (GameMap map : plugin.getMapManager().getMaps()) {
            if (map.getName().toLowerCase().replace(" ", "_").replace("'", "").equalsIgnoreCase(input) || map.getUrl().equalsIgnoreCase(input)) {
                gameMap = map;
            }
        }
        
        if (gameMap == null) {
            actor.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a map with that name or file name."));
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
