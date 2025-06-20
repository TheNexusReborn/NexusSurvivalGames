package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.bukkit.Material.*;

public class BlockListener implements Listener {
    public static final Set<Material> ALLOWED_BREAK = EnumSet.of(TALL_GRASS, SHORT_GRASS, POPPY, DANDELION, OAK_LEAVES, FIRE, COBWEB, MELON, CARROT, POTATO, CAKE), 
            NO_DROPS = EnumSet.of(DANDELION, TALL_GRASS, SHORT_GRASS, COCOA, POPPY), 
            ALLOWED_PLACE = EnumSet.of(CAKE, COBWEB, FIRE, TNT);
    
    private SurvivalGames plugin;

    public BlockListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (e.getBlock().getType() == Material.LADDER) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();

        if (lobby != null && lobby.checkMapEditing(e.getPlayer())) {
            return;
        }
        
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                e.setCancelled(true);
                return;
            }
            if (Stream.of(Game.State.INGAME, Game.State.INGAME_DEATHMATCH, Game.State.DEATHMATCH).anyMatch(gameState -> game.getState() == gameState)) {
                if (e.getBlock().getType() == Material.TNT) {
                    SGUtils.spawnTNTWithSource(e.getBlock().getLocation(), e.getPlayer(), game.getSettings().getTntFuseTicks(), game.getSettings().getTntYield());
                    new BukkitRunnable() {
                        public void run() {
                            e.getBlock().setType(AIR);
                        }
                    }.runTaskLater(plugin, 1L);
                } else {
                    if (!ALLOWED_PLACE.contains(e.getBlock().getType())) {
                        e.setCancelled(true);
                    }
                }
            } else {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();

        if (lobby != null && lobby.checkMapEditing(e.getPlayer())) {
            return;
        }
        
        if (game != null) {
            GamePlayer player = game.getPlayer(e.getPlayer().getUniqueId());
            if (!(player.getTeam() == GameTeam.TRIBUTES || player.getTeam() == GameTeam.MUTATIONS)) {
                e.setCancelled(true);
                return;
            }
            
            if (Stream.of(Game.State.INGAME, Game.State.INGAME_DEATHMATCH, Game.State.DEATHMATCH).anyMatch(gameState -> game.getState() == gameState)) {
                if (!ALLOWED_BREAK.contains(e.getBlock().getType())) {
                    e.setCancelled(true);
                }

                if (NO_DROPS.contains(e.getBlock().getType())) {
                    e.setCancelled(true);
                    e.getBlock().setType(Material.AIR);
                }
            } else {
                e.setCancelled(true);
            }

        } else {
            e.setCancelled(true);
        }
    }
}
