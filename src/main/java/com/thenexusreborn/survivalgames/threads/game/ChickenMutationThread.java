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
                game.logDebug("Handling Chicken Mutation Update for " + gamePlayer.getName());
                if (this.eggGain == 0) {
                    chickenMutation.incrementAmmunition();
                    game.logDebug("  Incremented Ammo");
                }
                
                game.logDebug("  Ammo: " + chickenMutation.getAmmunition());
                game.logDebug("  Chute Active: " + chickenMutation.isChuteActive());
                game.logDebug("  Chute Cooldown Left: " + chickenMutation.getParachuteCooldownTimer().getTime());
                game.logDebug("  Launch Left: " + chickenMutation.getLaunchCooldownTimer().getTime());
                game.logDebug("  Last Launch: " + chickenMutation.getLastEggLaunch());
                game.logDebug("  Current Time: " + System.currentTimeMillis());
                
                ItemStack hand = sgPlayer.getItemInHand();
                if (sgPlayer.isBlocking()) {
                    game.logDebug("  Player is Blocking");
                    if (hand != null) {
                        game.logDebug("  Item in Hand is Not Null");
                        if (hand.getType() == Material.WOOD_SWORD) {
                            game.logDebug("  Item in Hand is Wood Sword");
                            if (chickenMutation.getAmmunition() > 0) {
                                game.logDebug("  Ammo Amount is above 0");
                                if (chickenMutation.getLastEggLaunch() > 0) {
                                    if (chickenMutation.getLastEggLaunch() + game.getSettings().getChickenEggLaunchCooldown() >= System.currentTimeMillis()) {
                                        game.logDebug("  Egg Launch is off cooldown");
                                        sgPlayer.launchProjectile(Egg.class);
                                        chickenMutation.decrementAmmunition();
                                        chickenMutation.setLastEggLaunch(System.currentTimeMillis());
                                    }
                                } else {
                                    game.logDebug("  Egg Launcher has not been used");
                                    sgPlayer.launchProjectile(Egg.class);
                                    chickenMutation.decrementAmmunition();
                                    chickenMutation.setLastEggLaunch(System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
                
//                if (sgPlayer.isBlocking() && hand != null && hand.getType() == Material.WOOD_SWORD && chickenMutation.getAmmunition() > 0) {
//                    if (chickenMutation.getLastEggLaunch() > 0) {
//                        if (chickenMutation.getLastEggLaunch() + game.getSettings().getChickenEggLaunchCooldown() >= System.currentTimeMillis()) {
//                            sgPlayer.launchProjectile(Egg.class);
//                            chickenMutation.decrementAmmunition();
//                            chickenMutation.setLastEggLaunch(System.currentTimeMillis());
//                        }
//                    } else {
//                        sgPlayer.launchProjectile(Egg.class);
//                        chickenMutation.decrementAmmunition();
//                        chickenMutation.setLastEggLaunch(System.currentTimeMillis());
//                    }
//                }
                
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
