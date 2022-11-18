package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map.Entry;

public class MapSignUpdateTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public MapSignUpdateTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Lobby lobby = plugin.getLobby();
        if (lobby == null) {
            return;
        }
        
        if (plugin.getGame() != null) {
            return;
        }
        
        if (lobby.getState() == LobbyState.MAP_EDITING) {
            return;
        }
        
        if (lobby.getMapOptions().size() < 1) {
            return;
        }
        
        for (Entry<Integer, Location> entry : lobby.getMapSigns().entrySet()) {
            GameMap map = lobby.getMapOptions().get(entry.getKey());
            BlockState state = entry.getValue().getBlock().getState();
            if (!(state instanceof Sign sign)) {
                continue;
            }
            String mapName;
            if (map.getName().length() > 16) {
                mapName = map.getName().substring(0, 16);
            } else {
                mapName = map.getName();
            }
            sign.setLine(1, mapName);
            int votes = lobby.getTotalMapVotes(entry.getKey());
            
            sign.setLine(3, MCUtils.color("&n" + votes + " Vote(s)"));
            
            for (NexusPlayer player : lobby.getPlayers()) {
                Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                if (bukkitPlayer != null) {
                    World world = bukkitPlayer.getWorld();
                    if (world != null) {
                        if (!world.getName().equalsIgnoreCase(lobby.getSpawnpoint().getWorld().getName())) {
                            continue;
                        }
                        if (lobby.getMapVotes().get(entry.getKey()).contains(player.getUniqueId())) {
                            sign.setLine(0, MCUtils.color("&n#" + entry.getKey()));
                            sign.setLine(2, MCUtils.color("&2&lVOTED!"));
                        } else {
                            sign.setLine(0, MCUtils.color("&nClick to Vote"));
                            sign.setLine(2, "");
                        }
                        bukkitPlayer.sendSignChange(sign.getLocation(), sign.getLines());
                    }
                }
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 20L);
    }
}
