package com.thenexusreborn.survivalgames.threads.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.thenexusreborn.gamemaps.model.MapSpawn;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Stream;

public class WarmupSpawnThread extends NexusThread<SurvivalGames> {
    
    public static final GameState[] states = new GameState[] {GameState.WARMUP, GameState.WARMUP_DONE, GameState.DEATHMATCH_WARMUP, GameState.DEATHMATCH_WARMUP_DONE, GameState.TELEPORT_START, GameState.TELEPORT_DEATHMATCH, GameState.TELEPORT_START_DONE, GameState.TELEPORT_DEATHMATCH_DONE};
    
    public WarmupSpawnThread(SurvivalGames plugin) {
        super(plugin, 1L, 1L, false);
    }

    @Override
    public void onRun() {
        if (plugin.getGame() == null) {
            return;
        }

        Game game = plugin.getGame();
        SGMap gameMap = game.getGameMap();

        if (Stream.of(states).noneMatch(gameState -> game.getState() == gameState)) {
            return;
        }

        try {
            BiMap<UUID, Integer> spawns = HashBiMap.create(game.getSpawns()).inverse();
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
                    spawnLocation.setYaw(playerLocation.getYaw());
                    spawnLocation.setPitch(playerLocation.getPitch());
                    player.teleport(spawnLocation);
                }
            }
        } catch (Exception e) {}
    }
}
