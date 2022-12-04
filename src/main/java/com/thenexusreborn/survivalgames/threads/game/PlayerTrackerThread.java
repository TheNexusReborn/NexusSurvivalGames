package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTrackerThread extends NexusThread<SurvivalGames> {

    public PlayerTrackerThread(SurvivalGames plugin) {
        super(plugin, 2L, true);
    }

    public void onRun() {
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
            players.add(player.getUniqueId());
        }

        for (UUID p : players) {
            Player player = Bukkit.getPlayer(p);
            if (player == null) continue;
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
            if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                target = Bukkit.getPlayer(gamePlayer.getMutation().getTarget());
            } else {
                for (UUID u : players) {
                    Player t = Bukkit.getPlayer(u);
                    if (u.equals(p)) {
                        continue;
                    }

                    GamePlayer targetPlayer = game.getPlayer(u);
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
}
