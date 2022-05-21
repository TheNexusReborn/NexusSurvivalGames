package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

import static org.bukkit.Material.*;

public class BlockListener implements Listener {
    public static final Set<Material> ALLOWED_BREAK, NO_DROPS, ALLOWED_PLACE;
    
    static {
        ALLOWED_BREAK = new HashSet<>(Arrays.asList(LONG_GRASS, RED_ROSE, YELLOW_FLOWER, LEAVES, FIRE, WEB, MELON_BLOCK, CROPS, CARROT, POTATO, LEAVES, LEAVES_2, CAKE_BLOCK, DOUBLE_PLANT));
        NO_DROPS = new HashSet<>(Arrays.asList(YELLOW_FLOWER, LONG_GRASS, COCOA, RED_ROSE));
        ALLOWED_PLACE = new HashSet<>(Arrays.asList(CAKE_BLOCK, WEB, FIRE, TNT));
    }
    
    private SurvivalGames plugin;
    
    public BlockListener(SurvivalGames plugin) {
        this.plugin = plugin;
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
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS || gamePlayer.getTeam() == GameTeam.HIDDEN_STAFF) {
                e.setCancelled(true);
                return;
            }
            if (game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH || game.getState() == GameState.DEATHMATCH) {
                if (e.getBlock().getType() == Material.TNT) {
                    TNTPrimed entity = (TNTPrimed) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
                    entity.setFuseTicks(20);
                    entity.setYield(3.0F);
                    EntityTNTPrimed nmsTnt = ((CraftTNTPrimed) entity).getHandle();
                    try {
                        Field source = nmsTnt.getClass().getDeclaredField("source");
                        source.setAccessible(true);
                        source.set(nmsTnt, ((CraftPlayer) e.getPlayer()).getHandle());
                    } catch (Exception ex) {}
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
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME_DEATHMATCH || game.getState() == GameState.DEATHMATCH) {
                if (game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                    e.setCancelled(true);
                    return;
                }
    
                if (!ALLOWED_BREAK.contains(e.getBlock().getType())) {
                    e.setCancelled(true);
                }
    
                if (NO_DROPS.contains(e.getBlock().getType())) {
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
