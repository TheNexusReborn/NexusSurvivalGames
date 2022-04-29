package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.nexuscore.util.helper.NumberHelper;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTrackerTask extends BukkitRunnable {
    
    private SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    public void run() {
        Game game = plugin.getGame();
        long start = System.currentTimeMillis();
        if (game == null) {
            return;
        }
        
        if (!(game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal())) {
            return;
        }
        
        Set<UUID> players = new HashSet<>();
        for (GamePlayer player : game.getPlayers().values()) {
            if (player.getTeam() == GameTeam.TRIBUTES || player.getTeam() == GameTeam.MUTATIONS) {
                players.add(player.getUniqueId());
            }
        }
        
        for (UUID p : players) {
            Player player = Bukkit.getPlayer(p);
            boolean trackerInHotbar = false;
            boolean holdingTracker = false;
            for (int i = 0; i < 9; i++) {
                if (!trackerInHotbar) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item == null) {
                        continue;
                    }
                    trackerInHotbar = item.getType() == Material.COMPASS;
                }
            }
            
            if (trackerInHotbar) {
                ItemStack hand = player.getInventory().getItemInHand();
                if (hand != null) {
                    holdingTracker = hand.getType() == Material.COMPASS;
                }
            }
            
            Player target = null;
            double distance = -1;
            GamePlayer gamePlayer = game.getPlayer(p);
            //if (game.getMutationsTeam().isMember(p)) {
            //target = Bukkit.getPlayer(gamePlayer.getMutationTarget());
            //} else {
            for (UUID u : players) {
                Player t = Bukkit.getPlayer(u);
                if (u.equals(p)) {
                    continue;
                }
                double pd = player.getLocation().distance(t.getLocation());
                if (target == null) {
                    target = t;
                    distance = pd;
                } else if (distance == -1) {
                    target = t;
                    distance = pd;
                } else {
                    if (pd < distance) {
                        target = t;
                        distance = pd;
                    }
                }
            }
            //}
            
            if (target == null) {
                continue;
            }
            
            if (distance == -1) {
                distance = player.getLocation().distance(target.getLocation());
            }
            
            Player finalClosest = target;
            new BukkitRunnable() {
                public void run() {
                    player.setCompassTarget(finalClosest.getLocation());
                }
            }.runTask(plugin);
            
            String health = NumberHelper.formatNumber(target.getHealth());
            String maxHealth = NumberHelper.formatNumber(target.getMaxHealth());
            String targetName = "";
            
            if (holdingTracker) {
                gamePlayer.setTrackerInfo(new TrackerInfo(target.getName(), (int) distance, health, maxHealth));
            } else {
                gamePlayer.setTrackerInfo(null);
            }
        }
        
        long end = System.currentTimeMillis();
        long totalTime = end - start;
        if (totalTime > 20) {
            plugin.getLogger().severe("Player Tracker task took " + totalTime);
        }
        
    }
    
    public PlayerTrackerTask start() {
        runTaskTimerAsynchronously(plugin, 20L, 2L);
        return this;
    }
}
