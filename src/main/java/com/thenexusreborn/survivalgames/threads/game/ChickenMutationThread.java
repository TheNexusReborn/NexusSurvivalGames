package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChickenMutationThread extends StarThread<SurvivalGames> {
    
    private int eggGain = 19;
    
    public ChickenMutationThread(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
    }
    
    @Override
    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                continue;
            }
            Game game = sgPlayer.getGame();
            
            if (game == null) {
                continue;
            }
            
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
}
