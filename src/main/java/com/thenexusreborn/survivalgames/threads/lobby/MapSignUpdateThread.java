package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class MapSignUpdateThread extends NexusThread<SurvivalGames> {
    
    public MapSignUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            if (lobby.getState() == LobbyState.MAP_EDITING) {
                continue;
            }

            if (lobby.getMapOptions().isEmpty()) {
                continue;
            }

            for (Entry<Integer, Location> entry : lobby.getMapSigns().entrySet()) {
                SGMap map = lobby.getMapOptions().get(entry.getKey());
                if (map == null) {
                    continue;
                }
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

                for (LobbyPlayer player : lobby.getPlayers()) {
                    Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                    if (bukkitPlayer != null) {
                        World world = bukkitPlayer.getWorld();
                        if (world != null) {
                            if (!world.getName().equalsIgnoreCase(lobby.getSpawnpoint().getWorld().getName())) {
                                continue;
                            }
                            if (player.getMapVote() == entry.getKey()) {
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
    }
}
