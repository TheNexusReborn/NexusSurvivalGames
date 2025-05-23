package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.inventory.ItemStack;

public class ChickenMutationThread extends StarThread<SurvivalGames> {
    
    public static final int EGG_START_VALUE = 9;
    
    private int eggGain = EGG_START_VALUE;
    
    public ChickenMutationThread(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
    }
    
    @Override
    public void onRun() {
        for (SGPlayer sgPlayer : plugin.getPlayerRegistry()) {
            if (sgPlayer == null) {
                continue;
            }
            
            if (!sgPlayer.isOnline()) {
                continue;
            }
            
            Game game = sgPlayer.getGame();
            
            if (game == null) {
                continue;
            }
            
            GamePlayer gamePlayer = game.getPlayer(sgPlayer.getUniqueId());
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
                
                ItemStack hand = sgPlayer.getItemInHand();
                if (sgPlayer.isBlocking() && hand != null && hand.getType() == Material.WOOD_SWORD && chickenMutation.getAmmunition() > 0) {
                    sgPlayer.launchProjectile(Egg.class);
                    chickenMutation.decrementAmmunition();
                }
                
                sgPlayer.setLevel(chickenMutation.getAmmunition());
                
                if (chickenMutation.isChuteActive()) {
                    Location location = sgPlayer.getLocation();
                    if (location != null) {
                        location.setY(location.getBlockY() - 1);
                        if (location.getBlock().getType() == Material.AIR) {
                            sgPlayer.setVelocity(sgPlayer.getVelocity().setY(-0.2));
                        } else {
                            chickenMutation.deactivateChute();
                        }
                    }
                }
            }
        }
        
        if (eggGain <= 0) {
            eggGain = EGG_START_VALUE;
        } else {
            eggGain--;
        }
    }
}
