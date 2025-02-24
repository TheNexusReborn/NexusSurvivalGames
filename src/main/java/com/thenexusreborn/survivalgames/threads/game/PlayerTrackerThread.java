package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class PlayerTrackerThread extends StarThread<SurvivalGames> {

    public PlayerTrackerThread(SurvivalGames plugin) {
        super(plugin, 2L, true);
    }

    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }

            long start = System.currentTimeMillis();
            if (game == null) {
                continue;
            }

            if (!(game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal())) {
                continue;
            }

            Set<GamePlayer> players = new HashSet<>(game.getPlayers().values());

            for (GamePlayer gamePlayer : players) {
                Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
                if (player == null) {
                    continue;
                }
                boolean holdingTracker = false;

                ItemStack hand = player.getInventory().getItemInHand();
                if (hand != null) {
                    holdingTracker = hand.getType() == Material.COMPASS;
                }

                if (!holdingTracker) {
                    gamePlayer.setTrackerInfo(null);
                    continue;
                }

                Player target = null;
                double distance = -1;
                if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                    target = Bukkit.getPlayer(gamePlayer.getMutation().getTarget());
                } else {
                    for (GamePlayer targetPlayer : players) {
                        if (targetPlayer.getUniqueId().equals(gamePlayer.getUniqueId())) {
                            continue;
                        }
                        
                        Player t = Bukkit.getPlayer(targetPlayer.getUniqueId());
                        
                        if (targetPlayer.getTeam() != GameTeam.TRIBUTES) {
                            continue;
                        }

                        if (player.getLocation().getWorld().getName().equals(t.getLocation().getWorld().getName())) {
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
                    }
                }

                if (target == null) {
                    continue;
                }

                if (distance == -1) {
                    distance = player.getLocation().distance(target.getLocation());
                }
                
                if (!player.getLocation().getWorld().getName().equals(target.getLocation().getWorld().getName())) {
                    continue;
                }

                Player finalClosest = target;
                new BukkitRunnable() {
                    public void run() {
                        player.setCompassTarget(finalClosest.getLocation());
                    }
                }.runTask(plugin);

                String health = MCUtils.formatNumber(target.getHealth());
                String maxHealth = MCUtils.formatNumber(target.getMaxHealth());

                gamePlayer.setTrackerInfo(new TrackerInfo(target.getName(), (int) distance, health, maxHealth));
            }

            long end = System.currentTimeMillis();
            long totalTime = end - start;
            if (totalTime > 30) {
                plugin.getLogger().severe("Player Tracker task took " + totalTime);
            }
        }
    }
}
