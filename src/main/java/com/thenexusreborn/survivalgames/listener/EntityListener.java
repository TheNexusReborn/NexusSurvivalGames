package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.impl.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class EntityListener implements Listener {
    
    private final SurvivalGames plugin;
    
    private static final Set<DamageCause> GRACE_DAMAGE_STOP = new HashSet<>(Arrays.asList(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.FIRE, DamageCause.FIRE_TICK));
    
    public EntityListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            if (!(e.getEntity() instanceof Player)) {
                return;
            }
            
            GamePlayer gamePlayer = game.getPlayer(e.getEntity().getUniqueId());
            
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                e.setCancelled(true);
                e.getEntity().setFireTicks(0);
                return;
            } else if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                MutationType mutationType = gamePlayer.getMutation().getType();
                if (mutationType.getDamageImmunities().contains(e.getCause())) {
                    e.setCancelled(true);
                }
            }
            
            if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                if (GRACE_DAMAGE_STOP.contains(e.getCause())) {
                    e.setCancelled(true);
                }
            }
            
            GameState state = game.getState();
            if ((game.getState().ordinal() >= GameState.SETTING_UP.ordinal() && game.getState().ordinal() <= GameState.WARMUP_DONE.ordinal()) ||
                    (game.getState().ordinal() >= GameState.DEATHMATCH_WARMUP.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH_WARMUP_DONE.ordinal()) ||
                    (game.getState().ordinal() >= GameState.ENDING.ordinal() && game.getState().ordinal() <= GameState.ENDED.ordinal())) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            
            if (e.getDamager() instanceof Player) {
                GamePlayer gamePlayer = game.getPlayer(e.getDamager().getUniqueId());
                if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player target = (Player) e.getEntity();
    
                GamePlayer damagerPlayer = game.getPlayer(damager.getUniqueId());
                GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                    e.setCancelled(true);
                }
                
                checkMutationDamage(damagerPlayer, targetPlayer, e);
            } else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand) {
                e.setCancelled(true);
            } else if (e.getDamager() instanceof Projectile) {
                if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg) {
                    if (e.getEntity() instanceof Player) {
                        Player targetPlayer = (Player) e.getEntity();
                        if (e.getDamager() instanceof Snowball) {
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0));
                        } else {
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 160, 1));
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1));
                        }
    
                        if (e.getDamager() instanceof Egg) {
                            ProjectileSource shooter = ((Egg) e.getDamager()).getShooter();
                            if (shooter instanceof Player) {
                                Player shooterPlayer = (Player) shooter;
                                Mutation mutation = game.getPlayer(shooterPlayer.getUniqueId()).getMutation();
                                if (mutation instanceof ChickenMutation) {
                                    if (mutation.getTarget().equals(targetPlayer.getUniqueId())) {
                                        targetPlayer.damage(2.5, shooterPlayer);
                                    } else {
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                } else if (e.getDamager() instanceof Arrow) {
                    if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                        e.setCancelled(true);
                    }
    
                    ProjectileSource shooter = ((Arrow) e.getDamager()).getShooter();
                    if (shooter instanceof Player && e.getEntity() instanceof Player) {
                        checkMutationDamage(game.getPlayer(((Player) shooter).getUniqueId()), game.getPlayer(e.getEntity().getUniqueId()), e);
                    }
                }
            } else if (e.getDamager() instanceof TNTPrimed) {
                if (e.getEntity() instanceof Player) {
                    if (game.getSuicideLocations().containsKey(e.getDamager().getLocation())) {
                        UUID source = game.getSuicideLocations().get(e.getDamager().getLocation());
                        GamePlayer sourcePlayer = game.getPlayer(source);
                        if (sourcePlayer.getTeam() == GameTeam.MUTATIONS) {
                            if (!sourcePlayer.getMutation().getTarget().equals(e.getEntity().getUniqueId())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                    
                    TNTPrimed tntPrimed = (TNTPrimed) e.getDamager();
                    if (tntPrimed.getSource() instanceof Player) {
                        GamePlayer sourcePlayer = game.getPlayer(tntPrimed.getSource().getUniqueId());
                        if (sourcePlayer.getTeam() == GameTeam.MUTATIONS) {
                            if (!sourcePlayer.getMutation().getTarget().equals(e.getEntity().getUniqueId())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        } else {
            e.setCancelled(true);
        }
    }
    
    private void checkMutationDamage(GamePlayer damagerPlayer, GamePlayer targetPlayer, EntityDamageByEntityEvent e) {
        if (damagerPlayer.getTeam() == GameTeam.MUTATIONS) {
            if (!damagerPlayer.getMutation().getTarget().equals(targetPlayer.getUniqueId())) {
                e.setCancelled(true);
                damagerPlayer.sendMessage("&4&l>> &cYou can only damage your target.");
            }
        } else if (targetPlayer.getTeam() == GameTeam.MUTATIONS) {
            if (!targetPlayer.getMutation().getTarget().equals(damagerPlayer.getUniqueId())) {
                e.setCancelled(true);
                damagerPlayer.sendMessage("&4&l>> &cYou can only damage mutations that are after you.");
            } else {
                if (targetPlayer.getMutation() instanceof SkeletonMutation) {
                    e.setDamage(e.getDamage() * 1.5);
                }
            }
        }
    }
    
    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            if (e.getEntity() instanceof Player) {
                Player player = ((Player) e.getEntity());
                GamePlayer gamePlayer = game.getPlayer(e.getEntity().getUniqueId());
                if (!game.getSettings().isRegeneration()) {
                    if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                        e.setCancelled(true);
                    }
                }
                
                if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                    MutationType mutationType = gamePlayer.getMutation().getType();
                    if (!mutationType.healthRegen()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
