package com.thenexusreborn.survivalgames.threads.lobby;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class MapSignUpdateThread extends StarThread<SurvivalGames> {
    
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

                sign.setLine(3, ColorHandler.getInstance().color("&n" + votes + " Vote(s)"));

                for (LobbyPlayer player : lobby.getPlayers()) {
                    Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                    if (bukkitPlayer != null) {
                        World world = bukkitPlayer.getWorld();
                        if (world != null) {
                            if (!world.getName().equalsIgnoreCase(lobby.getSpawnpoint().getWorld().getName())) {
                                continue;
                            }
                            if (player.getMapVote() == entry.getKey()) {
                                sign.setLine(0, ColorHandler.getInstance().color("&n#" + entry.getKey()));
                                sign.setLine(2, ColorHandler.getInstance().color("&2&lVOTED!"));
                            } else {
                                sign.setLine(0, ColorHandler.getInstance().color("&nClick to Vote"));
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
