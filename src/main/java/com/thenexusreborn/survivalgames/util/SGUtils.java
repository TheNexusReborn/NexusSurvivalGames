package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public final class SGUtils {
    
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    public static void setMapNameForScoreboard(SGMap map, ITeam team) {
        if (map != null) {
            setTeamValueForString(map.getName(), team);
        } else {
            team.setPrefix("&fNot Set");
        }
    }
    
    public static void setTeamValueForString(String string, ITeam team) {
        String prefix = "&f", suffix = "&f";
        if (string.length() > 14) {
            prefix += string.substring(0, 14);
            suffix += string.substring(14);
        } else {
            prefix += string;
        }
    
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }
    
    public static void spawnTNTWithSource(Location location, Player player, int fuseTicks, float yield) {
        TNTPrimed entity = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        entity.setFuseTicks(fuseTicks);
        entity.setYield(yield);
        EntityTNTPrimed nmsTnt = ((CraftTNTPrimed) entity).getHandle();
        try {
            Field source = nmsTnt.getClass().getDeclaredField("source");
            source.setAccessible(true);
            source.set(nmsTnt, ((CraftPlayer) player).getHandle());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void updatePlayerHealthAndFood(Player player) {
        if (player == null) {
            return;
        }
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(2);
    }
    
    public static void sendToHub(Player player) {
        //TODO Handle this
    }
    
    public static String getMapNameFromCommand(String[] args, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }
    
    public static SGMap getLoadedGameMap(String input, CommandSender actor) {
        SGMap gameMap = getGameMapFromInput(input, actor);
        if (gameMap == null) {
            return null;
        }
        
        if (gameMap.getWorld() == null) {
            actor.sendMessage(MCUtils.color(MsgType.WARN + "That map is not loaded, please load before teleporting."));
            return null;
        }
        return gameMap;
    }
    
    public static SGMap getGameMapFromInput(String input, CommandSender actor) {
        SGMap gameMap = null;
        for (SGMap map : plugin.getMapManager().getMaps()) {
            if (map.getName().toLowerCase().replace(" ", "_").replace("'", "").equalsIgnoreCase(input) || map.getUrl().equalsIgnoreCase(input)) {
                gameMap = map;
            }
        }
        
        if (gameMap == null) {
            actor.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a map with that name or file name."));
        }
        
        return gameMap;
    }
    
    public static String getHandItemName(ItemStack handItem) {
        if (handItem != null && handItem.getType() != Material.AIR) {
            if (!handItem.hasItemMeta() || handItem.getItemMeta().getDisplayName() == null) {
                return handItem.getType().name().toLowerCase().replace("_", " ");
            } else {
                return handItem.getItemMeta().getDisplayName();
            }
        } else {
            return "their fists";
        }
    }
}
