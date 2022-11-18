package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ChickenMutationTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    private int eggGain = 19;
    
    public ChickenMutationTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                continue;
            }
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                continue;
            }
        
            if (gamePlayer.getMutation() instanceof ChickenMutation chickenMutation) {
    
                if (this.eggGain == 0) {
                    chickenMutation.incrementAmmunition();
                }
                
                ItemStack hand = player.getItemInHand();
                if (player.isBlocking() && hand != null && hand.getType() == Material.WOOD_SWORD && chickenMutation.getAmmunition() > 0) {
                    player.launchProjectile(Egg.class);
                    chickenMutation.decrementAmmunition();
                }
    
                player.setLevel(chickenMutation.getAmmunition());
                
                if (chickenMutation.isChuteActive()) {
                    Location location = player.getLocation();
                    location.setY(location.getBlockY() - 1);
                    if (location.getBlock().getType() == Material.AIR) {
                        player.setVelocity(player.getVelocity().setY(-0.2));
                    } else {
                        chickenMutation.deactivateChute();
                    }
                }
            }
        }
        
        if (eggGain <= 0) {
            eggGain = 19;
        } else {
            eggGain--;
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 1L);
    }
}
