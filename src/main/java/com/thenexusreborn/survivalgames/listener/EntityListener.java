package com.thenexusreborn.survivalgames.listener;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.MutationType;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import com.thenexusreborn.survivalgames.mutations.impl.SkeletonMutation;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityListener implements Listener {

    private final SurvivalGames plugin;

    private static final Set<DamageCause> GRACE_DAMAGE_STOP = new HashSet<>(Arrays.asList(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION, DamageCause.FIRE, DamageCause.FIRE_TICK));

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
        if ((e.getCause() == RemoveCause.EXPLOSION || e.getCause() == RemoveCause.ENTITY)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();

        if (game != null) {
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

            if (game.isGraceperiod()) {
                if (GRACE_DAMAGE_STOP.contains(e.getCause())) {
                    e.setCancelled(true);
                }
            }

            if (game.getState().ordinal() >= GameState.SETTING_UP.ordinal() && game.getState().ordinal() <= GameState.WARMUP_DONE.ordinal() ||
                    game.getState().ordinal() >= GameState.DEATHMATCH_WARMUP.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH_WARMUP_DONE.ordinal() ||
                    game.getState().ordinal() >= GameState.ENDING.ordinal() && game.getState().ordinal() <= GameState.ENDED.ordinal()) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Game game = null;
        World eventWorld = e.getEntity().getWorld();
        for (SGVirtualServer server : plugin.getServers().getObjects().values()) {
            if (server.getGame() == null) {
                continue;
            }
            if (server.getGame().getGameMap().getWorld().equals(eventWorld)) {
                game = server.getGame();
                break;
            }
        }

        if (game == null) {
            Lobby lobby = null;
            for (SGVirtualServer server : plugin.getServers().getObjects().values()) {
                if (server.getLobby() == null) {
                    continue;
                }
                if (server.getLobby().getWorld().equals(eventWorld)) {
                    lobby = server.getLobby();
                }
            }

            if (lobby != null) {
                e.setCancelled(true);
            }

            return;
        }

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
        } else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand) {
            e.setCancelled(true);
        } else if (e.getDamager() instanceof Projectile projectile) {
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
                    MutationType mutationType = gamePlayer.getMutation().getType();
                    if (!mutationType.healthRegen()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
