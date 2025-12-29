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
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class GameBoundsThread extends StarThread<SurvivalGames> {
    
    public GameBoundsThread(SurvivalGames plugin) {
        super(plugin, 20L, 20L, false);
    }
    
    @Override
    public void onRun() {
        for (SGPlayer sgPlayer : plugin.getPlayerRegistry().values()) {
            Game game = sgPlayer.getGame();
            if (game == null) {
                continue;
            }
            
            if (sgPlayer.getGamePlayer() == null) {
                continue;
            }
            
            SGMap map = game.getGameMap();
            
            Region region;
            if (game.getState() == Game.State.INGAME || game.getState() == Game.State.INGAME_DEATHMATCH) {
                region = map.getArenaRegion();
            } else if (game.getState() == Game.State.DEATHMATCH) {
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
                if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                    game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.LEAVE_ARENA, gamePlayer.getPosition().toLocation(game.getGameMap().getWorld())));
                    return;
                }
                
                if (region.contains(previous)) {
                    player.teleport(previousLoc);
                    gamePlayer.sendMessage(MsgType.WARN.format("You cannot exit the arena."));
                } else {
                    Location mapSpawnCenter = map.getSpawnCenter().toLocation(world);
                    game.teleportSpectator(player, mapSpawnCenter);
                    gamePlayer.sendMessage(MsgType.WARN.format("You cannot exit the arena. Teleported you to the center"));
                }
            }
        }
    }
}
