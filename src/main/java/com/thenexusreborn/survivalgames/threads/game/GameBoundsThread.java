package com.thenexusreborn.survivalgames.threads.game;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class GameBoundsThread extends StarThread<SurvivalGames> {
    
    public GameBoundsThread(SurvivalGames plugin) {
        super(plugin, 20L, 20L, false);
    }

    @Override
    public void onRun() {
        for (SGPlayer sgPlayer : plugin.getPlayerRegistry()) {
            Game game = sgPlayer.getGame();
            if (game == null) {
                continue;
            }

            if (sgPlayer.getGamePlayer() == null) {
                continue;
            }

            SGMap map = game.getGameMap();

            Region region;
            if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH) {
                region = map.getArenaRegion();
            } else if (game.getState() == GameState.DEATHMATCH) {
                region = map.getDeathmatchRegion();
            } else {
                continue;
            }

            if (region == null) {
                continue;
            }

            GamePlayer gamePlayer = sgPlayer.getGamePlayer();
            Player player = Bukkit.getPlayer(sgPlayer.getUniqueId());
            World world = map.getWorld();
            Location previousLoc = gamePlayer.getPosition().toLocation(world);
            Vector previous = BukkitUtil.toVector(previousLoc);
            gamePlayer.setPosition(player.getLocation());
            Vector current = BukkitUtil.toVector(player.getLocation());
            
            if (!region.contains(current)) {
                if (region.contains(previous)) {
                    player.teleport(previousLoc);
                    gamePlayer.sendMessage(MsgType.WARN.format("You cannot exit the arena."));
                } else {
                    Location mapSpawnCenter = map.getSpawnCenter().toLocation(world);
                    if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                        game.teleportSpectator(player, mapSpawnCenter);
                        gamePlayer.sendMessage(MsgType.WARN.format("You cannot exit the arena. Teleported you to the center"));
                    } else {
                        game.getSpawns().forEach((index, pid) -> {
                            if (pid != null && pid.equals(player.getUniqueId())) {
                                game.teleportTribute(player, map.getSpawns().get(index).toGameLocation(world, mapSpawnCenter));
                            } else {
                                game.teleportTribute(player, map.getSpawns().getFirst().toGameLocation(world, mapSpawnCenter));
                            }
                            gamePlayer.sendMessage(MsgType.WARN.format("You were found outside of the arena bounds and your previous location was not within it, you were teleported to a spawnpoing"));
                        });
                    }
                }
            }
        }
    }
}
