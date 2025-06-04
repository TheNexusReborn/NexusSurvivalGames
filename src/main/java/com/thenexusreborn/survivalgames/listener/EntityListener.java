package com.thenexusreborn.survivalgames.listener;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.Game.SubState;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class EntityListener implements Listener {
    
    private final SurvivalGames plugin;
    
    private static final Set<DamageCause> GRACE_DAMAGE_STOP = EnumSet.of(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.FIRE, DamageCause.FIRE_TICK);
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.#");
    
    public EntityListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof Player target)) {
            return;
        }
        
        SGPlayer targetPlayer = plugin.getPlayerRegistry().get(target.getUniqueId());
        if (targetPlayer.getGame() != null) {
            if (targetPlayer.getGamePlayer().getTeam() == GameTeam.SPECTATORS || targetPlayer.getGamePlayer().getTeam() == GameTeam.MUTATIONS) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
        if (e.getCause() == RemoveCause.EXPLOSION || e.getCause() == RemoveCause.ENTITY) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        NexusReborn.sendDebugMessage("Handling EntityDamageEvent in SurvivalGames...");
        NexusReborn.sendDebugMessage("  Target: " + (e.getEntity() instanceof Player player ? player.getName() : e.getEntity().getType().name()));
        NexusReborn.sendDebugMessage("  Cause: " + e.getCause().name());
        World eventWorld = e.getEntity().getWorld();
        NexusReborn.sendDebugMessage("  - World: " + eventWorld.getName());
        NexusReborn.sendDebugMessage("  Damage Values (Original - Modified)");
        for (DamageModifier modifier : DamageModifier.values()) {
            if (e.isApplicable(modifier)) {
                NexusReborn.sendDebugMessage("    " + modifier.name() + ": " + e.getOriginalDamage(modifier) + " - " + e.getDamage(modifier));
            }
        }
        
        NexusReborn.sendDebugMessage("    ACTUAL: " + e.getDamage() + " - " + e.getFinalDamage());
        
        if (e.getEntity() instanceof Item) {
            if (e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION) {
                e.setCancelled(true);
                return;
            }
        } else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand || e.getEntity() instanceof Villager) {
            e.setCancelled(true);
            return;
        }
        
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        if (game != null) {
            NexusReborn.sendDebugMessage("  Game State: " + game.getState().name() + (game.getSubState() != SubState.UNDEFINED ? "." + game.getSubState().name() : ""));
            GamePlayer gamePlayer = game.getPlayer(e.getEntity().getUniqueId());
            NexusReborn.sendDebugMessage("  Target Team: " + gamePlayer.getTeam().name());
            
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                e.setCancelled(true);
                e.getEntity().setFireTicks(0);
                return;
            } else if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                Mutation mutation = gamePlayer.getMutation();
                if (mutation == null) {
                    SurvivalGames.getInstance().getLogger().warning("Entity Damage Event called for a mutation that is null.");
                    return;
                }
                
                IMutationType IMutationType = mutation.getType();
                if (IMutationType.getDamageImmunities().contains(e.getCause())) {
                    e.setCancelled(true);
                }
            }
            
            if (game.isGraceperiod()) {
                if (GRACE_DAMAGE_STOP.contains(e.getCause())) {
                    e.setCancelled(true);
                }
            }
            
            if (game.getState().ordinal() >= Game.State.SETTING_UP.ordinal() && game.getState().ordinal() <= Game.State.WARMUP_DONE.ordinal() ||
                    game.getState().ordinal() >= Game.State.DEATHMATCH_WARMUP.ordinal() && game.getState().ordinal() <= Game.State.DEATHMATCH_WARMUP_DONE.ordinal() ||
                    game.getState().ordinal() >= Game.State.ENDING.ordinal() && game.getState().ordinal() <= Game.State.ENDED.ordinal()) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
        
        long end = System.currentTimeMillis();
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        NexusReborn.sendDebugMessage("Handling EntityDamageByEntityEvent in SurvivalGames...");
        NexusReborn.sendDebugMessage("  Damager: " + (e.getDamager() instanceof Player player ? player.getName() : e.getDamager().getType().name()));
        NexusReborn.sendDebugMessage("  Target: " + (e.getEntity() instanceof Player player ? player.getName() : e.getEntity().getType().name()));
        NexusReborn.sendDebugMessage("  Cause: " + e.getCause().name());
        World eventWorld = e.getEntity().getWorld();
        NexusReborn.sendDebugMessage("  - World: " + eventWorld.getName());
        NexusReborn.sendDebugMessage("  Damage Values (Original - Modified)");
        for (DamageModifier modifier : DamageModifier.values()) {
            if (e.isApplicable(modifier)) {
                NexusReborn.sendDebugMessage("    " + modifier.name() + ": " + DECIMAL_FORMAT.format(e.getOriginalDamage(modifier)) + " - " + DECIMAL_FORMAT.format(e.getDamage(modifier)));
            }
        }
        
        NexusReborn.sendDebugMessage("    ACTUAL: " + e.getDamage() + " - " + e.getFinalDamage());
        
        Game game = null;
        Lobby lobby = null;
        
        for (SGVirtualServer server : plugin.getServers().getObjects().values()) {
            if (server.getGame() != null) {
                if (server.getGame().getGameMap().getWorld().equals(eventWorld)) {
                    game = server.getGame();
                    break;
                }
            }
            
            if (server.getLobby() != null) {
                if (server.getLobby().getWorld().equals(eventWorld)) {
                    lobby = server.getLobby();
                    break;
                }
            }
        }
        
        if (lobby != null) {
            e.setCancelled(true);
            return;
        }
        
        if (game == null) {
            return;
        }
        
        NexusReborn.sendDebugMessage("  Game State: " + game.getState().name() + (game.getSubState() != SubState.UNDEFINED ? "." + game.getSubState().name() : ""));
        
        if (e.getDamager() instanceof Player damager) {
            GamePlayer damagerPlayer = game.getPlayer(damager.getUniqueId());
            NexusReborn.sendDebugMessage("  Damager Team: " + damagerPlayer.getTeam().name());
            if (damagerPlayer.getTeam() == GameTeam.SPECTATORS) {
                e.setCancelled(true);
                return;
            }
            
            if (e.getEntity() instanceof Player target) {
                GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                NexusReborn.sendDebugMessage("  Target Team: " + targetPlayer.getTeam());
                if (game.isGraceperiod()) {
                    e.setCancelled(true);
                    return;
                }
                
                checkMutationDamage(damagerPlayer, targetPlayer, e);
                
                if (e.isCancelled()) {
                    return;
                }
                damagerPlayer.setCombat(targetPlayer);
                targetPlayer.setCombat(damagerPlayer);
                
                targetPlayer.getDamageInfo().addDamager(damager.getUniqueId());
            }
        }
        
        if (e.getDamager() instanceof Projectile projectile) {
            if (!(e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg || e.getDamager() instanceof Arrow || e.getDamager() instanceof FishHook)) {
                return;
            }
            
            if (!(e.getEntity() instanceof Player target)) {
                return;
            }
            
            if (!(projectile.getShooter() instanceof Player shooter)) {
                return;
            }
            
            if (game.isGraceperiod()) {
                shooter.sendMessage(StarColors.color("&6&l>> &cYou cannot harm others during grace period!"));
                e.setCancelled(true);
                return;
            }
            
            GamePlayer damagerPlayer = game.getPlayer(shooter.getUniqueId());
            GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
            damagerPlayer.setCombat(targetPlayer);
            targetPlayer.setCombat(damagerPlayer);
            if (targetPlayer.getUniqueId() != damagerPlayer.getUniqueId()) {
                targetPlayer.getDamageInfo().addDamager(damagerPlayer.getUniqueId());
            }
            
            checkMutationDamage(game.getPlayer(shooter.getUniqueId()), game.getPlayer(e.getEntity().getUniqueId()), e);
            if (e.isCancelled()) {
                return;
            }
            
            if (e.getDamager() instanceof Snowball) {
                if (targetPlayer.getTeam() == GameTeam.TRIBUTES) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0));
                } else {
                    e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Egg) {
                Mutation mutation = game.getPlayer(shooter.getUniqueId()).getMutation();
                if (targetPlayer.getTeam() == GameTeam.TRIBUTES) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 160, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1));
                } else {
                    if (!(mutation instanceof ChickenMutation)) {
                        e.setCancelled(true);
                    }
                }
                
                if (mutation instanceof ChickenMutation) {
                    if (mutation.getTarget().equals(target.getUniqueId())) {
                        for (DamageModifier damageModifier : DamageModifier.values()) {
                            try {
                                e.setDamage(damageModifier, 0);
                            } catch (Exception ex) {} 
                        }
                        
                        e.setDamage(DamageModifier.BASE, 2.5);
                        target.setLastDamageCause(new EntityDamageByEntityEvent(Bukkit.getPlayer(targetPlayer.getUniqueId()), e.getEntity(), DamageCause.ENTITY_ATTACK,  2.5));
                    } else {
                        e.setCancelled(true);
                    }
                }
            } else if (e.getDamager() instanceof FishHook) {
                if (!game.getSettings().isAllowRoddingMutations()) {
                    if (targetPlayer.getTeam() == GameTeam.MUTATIONS) {
                        Material blockType = damagerPlayer.getLocation().getBlock().getType();
                        if (blockType == Material.WATER || blockType == Material.STATIONARY_WATER) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        } else if (e.getDamager() instanceof TNTPrimed tntPrimed) {
            if (e.getEntity() instanceof Player target) {
                if (tntPrimed.getSource() instanceof Player) {
                    GamePlayer sourcePlayer = game.getPlayer(tntPrimed.getSource().getUniqueId());
                    if (sourcePlayer != null) {
                        if (sourcePlayer.getTeam() == GameTeam.MUTATIONS) {
                            if (!sourcePlayer.getMutation().getTarget().equals(e.getEntity().getUniqueId())) {
                                e.setCancelled(true);
                                return;
                            }
                        }
                        GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                        GamePlayer damagerPlayer = game.getPlayer(sourcePlayer.getUniqueId());
                        if (targetPlayer.getUniqueId() != damagerPlayer.getUniqueId()) {
                            targetPlayer.getDamageInfo().addDamager(damagerPlayer.getUniqueId());
                        }
                        damagerPlayer.setCombat(targetPlayer);
                        targetPlayer.setCombat(damagerPlayer);
                    }
                }
            }
        }
    }
    
    private void checkMutationDamage(GamePlayer damagerPlayer, GamePlayer targetPlayer, EntityDamageByEntityEvent e) {
        if (damagerPlayer.getTeam() == GameTeam.MUTATIONS) {
            Mutation mutation = damagerPlayer.getMutation();
            
            if (mutation == null) {
                SurvivalGames.getInstance().getLogger().warning("Entity Damage By Entity Event called for damager mutation that is null.");
                damagerPlayer.sendMessage(MsgType.ERROR.format("&lYou are set to the Mutation GameTeam, but your mutation information is null. Please report this to Firestar311."));
                e.setCancelled(true);
                return;
            }
            
            if (mutation.getTarget() == null) {
                damagerPlayer.sendMessage(MsgType.ERROR.format("&lYou are set to the Mutation GameTeam, but your target is null. Please report this to Firestar311."));
                e.setCancelled(true);
                return;
            }
            
            if (!mutation.getTarget().equals(targetPlayer.getUniqueId())) {
                e.setCancelled(true);
                damagerPlayer.sendMessage("&4&l>> &cYou can only damage your target.");
            }
        } else if (targetPlayer.getTeam() == GameTeam.MUTATIONS) {
            Mutation mutation = targetPlayer.getMutation();
            if (mutation == null) {
                SurvivalGames.getInstance().getLogger().warning("Entity Damage By Entity Event called for target mutation that is null.");
                targetPlayer.sendMessage(MsgType.ERROR.format("&lYou are set to the Mutation GameTeam, but your mutation information is null. Please report this to Firestar311."));
                e.setCancelled(true);
                return;
            }
            
            if (mutation.getTarget() == null) {
                targetPlayer.sendMessage(MsgType.ERROR.format("&lYou are set to the Mutation GameTeam, but your target is null. Please report this to Firestar311."));
                e.setCancelled(true);
                return;
            }
            
            if (!mutation.getTarget().equals(damagerPlayer.getUniqueId())) {
                e.setCancelled(true);
                damagerPlayer.sendMessage("&4&l>> &cYou can only damage mutations that are after you.");
            } else {
                if (mutation.getType().getModifiers().contains(MutationModifier.FIFTY_PERCENT_INCREASED_DAMAGE)) {
                    if (e.getCause() == DamageCause.ENTITY_ATTACK) {
                        e.setDamage(e.getDamage() * 1.5);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        if (game != null) {
            if (e.getEntity() instanceof Player) {
                GamePlayer gamePlayer = game.getPlayer(e.getEntity().getUniqueId());
                if (!game.getSettings().isRegeneration()) {
                    if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                        e.setCancelled(true);
                    }
                }
                
                if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                    Mutation mutation = gamePlayer.getMutation();
                    
                    if (mutation == null) {
                        SurvivalGames.getInstance().getLogger().warning("Entity Regain Health Event called for a mutation that is null.");
                        return;
                    }
                    
                    IMutationType IMutationType = mutation.getType();
                    if (IMutationType.getModifiers().contains(MutationModifier.NO_HEALTH_REGEN)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
