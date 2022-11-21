package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.impl.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.*;

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
    public void onEntityDamageByEntity(EntityDamageEvent e) {
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            
            if (e.getDamager() instanceof Player) {
                GamePlayer gamePlayer = game.getPlayer(e.getDamager().getUniqueId());
                if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player target) {
                GamePlayer damagerPlayer = game.getPlayer(damager.getUniqueId());
                GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                    e.setCancelled(true);
                    return;
                }
                
                checkMutationDamage(damagerPlayer, targetPlayer, e);
                
                if (e.isCancelled()) {
                    return;
                }
                damagerPlayer.setCombat(targetPlayer.getNexusPlayer());
                targetPlayer.setCombat(damagerPlayer.getNexusPlayer());
                
                targetPlayer.getDamageInfo().addDamager(damager.getUniqueId());
            } else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand) {
                e.setCancelled(true);
            } else if (e.getDamager() instanceof Projectile projectile) {
                if (!(e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg || e.getDamager() instanceof Arrow)) {
                    return;
                }
                
                if (!(e.getEntity() instanceof Player target)) {
                    return;
                }
                
                if (!(projectile.getShooter() instanceof Player shooter)) {
                    return;
                }
                
                if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                    shooter.sendMessage(MCUtils.color("&6&l>> &cYou cannot harm others during grace period!"));
                    e.setCancelled(true);
                    return;
                }
                
                GamePlayer damagerPlayer = game.getPlayer(shooter.getUniqueId());
                GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                damagerPlayer.setCombat(targetPlayer.getNexusPlayer());
                targetPlayer.setCombat(damagerPlayer.getNexusPlayer());
                targetPlayer.getDamageInfo().addDamager(damagerPlayer.getUniqueId());
                
                checkMutationDamage(game.getPlayer(shooter.getUniqueId()), game.getPlayer(e.getEntity().getUniqueId()), e);
                if (e.isCancelled()) {
                    return;
                }
                
                if (e.getDamager() instanceof Snowball) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0));
                } else if (e.getDamager() instanceof Egg) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 160, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1));
                    
                    Mutation mutation = game.getPlayer(shooter.getUniqueId()).getMutation();
                    if (mutation instanceof ChickenMutation) {
                        if (mutation.getTarget().equals(target.getUniqueId())) {
                            target.damage(2.5, shooter);
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            } else if (e.getDamager() instanceof TNTPrimed) {
                if (e.getEntity() instanceof Player target) {
                    TNTPrimed tntPrimed = (TNTPrimed) e.getDamager();
                    if (tntPrimed.getSource() instanceof Player) {
                        GamePlayer sourcePlayer = game.getPlayer(tntPrimed.getSource().getUniqueId());
                        if (sourcePlayer.getTeam() == GameTeam.MUTATIONS) {
                            if (!sourcePlayer.getMutation().getTarget().equals(e.getEntity().getUniqueId())) {
                                e.setCancelled(true);
                                return;
                            }
                        }
                        GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                        GamePlayer damagerPlayer = game.getPlayer(sourcePlayer.getUniqueId());
                        targetPlayer.getDamageInfo().addDamager(damagerPlayer.getUniqueId());
                        damagerPlayer.setCombat(targetPlayer.getNexusPlayer());
                        targetPlayer.setCombat(damagerPlayer.getNexusPlayer());
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
            if (e.getEntity() instanceof Player player) {
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
