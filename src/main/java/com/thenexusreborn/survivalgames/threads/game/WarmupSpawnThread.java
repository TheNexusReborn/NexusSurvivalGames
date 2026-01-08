package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.map.MapSpawn;
import com.thenexusreborn.survivalgames.map.SGMap;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

import static com.thenexusreborn.survivalgames.game.Game.State.*;

public class WarmupSpawnThread extends StarThread<SurvivalGames> {
    
    public static final Game.State[] states = new Game.State[] {WARMUP, WARMUP_DONE, DEATHMATCH_WARMUP, DEATHMATCH_WARMUP_DONE, TELEPORT_START, TELEPORT_DEATHMATCH, TELEPORT_START_DONE, TELEPORT_DEATHMATCH_DONE};
    
    public WarmupSpawnThread(SurvivalGames plugin) {
        super(plugin, 1L, 1L, false);
    }

    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers().values()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            SGMap gameMap = game.getGameMap();

            if (Stream.of(states).noneMatch(gameState -> game.getState() == gameState)) {
                continue;
            }

            try {
                Map<UUID, Integer> spawns = new HashMap<>();
                for (Map.Entry<Integer, UUID> entry : game.getSpawns().entrySet()) {
                    if (entry.getValue() != null) {
                        spawns.put(entry.getValue(), entry.getKey());
                    }
                }
                
                for (GamePlayer gamePlayer : game.getPlayers().values()) {
                    if (gamePlayer == null || gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                        continue;
                    }

                    Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
                    Location playerLocation = player.getLocation();
                    MapSpawn spawn = gameMap.getSpawns().get(spawns.get(player.getUniqueId()));
                    if (spawn == null) {
                        continue;
                    }

                    Location spawnLocation = spawn.toGameLocation(gameMap.getWorld(), gameMap.getCenterLocation());
                    if (playerLocation.getBlockX() != spawnLocation.getBlockX() || playerLocation.getBlockZ() != spawnLocation.getBlockZ()) {
                        spawnLocation.setY(playerLocation.getY() + 1);
                        spawnLocation.setYaw(playerLocation.getYaw());
                        spawnLocation.setPitch(playerLocation.getPitch());
                        player.teleport(spawnLocation);
                        gamePlayer.setPosition(spawnLocation);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
