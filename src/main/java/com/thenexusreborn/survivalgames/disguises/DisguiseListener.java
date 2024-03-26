package com.thenexusreborn.survivalgames.disguises;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.*;
import com.thenexusreborn.survivalgames.disguises.utilities.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class DisguiseListener implements Listener {

    private SurvivalGames plugin;

    public DisguiseListener(SurvivalGames libsDisguises) {
        plugin = libsDisguises;
    }

    private void checkPlayerCanBlowDisguise(Player entity) {
        Disguise[] disguises = DisguiseAPI.getDisguises(entity);
        if (disguises.length > 0) {
            DisguiseAPI.undisguiseToAll(entity);
        }
    }

    private void chunkMove(Player player, Location newLoc, Location oldLoc) {
        try {
            for (PacketContainer packet : DisguiseUtilities.getBedChunkPacket(player, newLoc, oldLoc)) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
            }
            if (newLoc != null) {
                for (HashSet<TargetedDisguise> list : DisguiseUtilities.getDisguises().values()) {
                    for (TargetedDisguise disguise : list) {
                        if (disguise.isPlayerDisguise() && disguise.canSee(player)
                                && ((PlayerDisguise) disguise).getWatcher().isSleeping()
                                && DisguiseUtilities.getPerverts(disguise).contains(player)) {
                            PacketContainer[] packets = DisguiseUtilities.getBedPackets(player,
                                    disguise.getEntity() == player ? newLoc : disguise.getEntity().getLocation(), newLoc,
                                    (PlayerDisguise) disguise);
                            if (disguise.getEntity() == player) {
                                for (PacketContainer packet : packets) {
                                    packet.getIntegers().write(0, DisguiseAPI.getSelfDisguiseId());
                                }
                            }
                            for (PacketContainer packet : packets) {
                                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                            }
                        }
                    }
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace(System.out);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (DisguiseConfig.isBedPacketsEnabled()) {
            chunkMove(p, p.getLocation(), null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (DisguiseConfig.isBedPacketsEnabled()) {
            Location to = event.getTo();
            Location from = event.getFrom();
            int x1 = (int) Math.floor(to.getX() / 16D) - 17;
            int x2 = (int) Math.floor(from.getX() / 16D) - 17;
            int z1 = (int) Math.floor(to.getZ() / 16D) - 17;
            int z2 = (int) Math.floor(from.getZ() / 16D) - 17;
            if (x1 - (x1 % 8) != x2 - (x2 % 8) || z1 - (z1 % 8) != z2 - (z2 % 8)) {
                chunkMove(event.getPlayer(), to, from);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ReflectionManager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Disguise[] disguises = DisguiseAPI.getDisguises(event.getPlayer());
        for (Disguise disguise : disguises) {
            if (disguise.isRemoveDisguiseOnDeath()) {
                disguise.removeDisguise();
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (DisguiseConfig.isMonstersIgnoreDisguises() && event.getTarget() != null && event.getTarget() instanceof Player
                && DisguiseAPI.isDisguised(event.getTarget())) {
            switch (event.getReason()) {
                case TARGET_ATTACKED_ENTITY:
                case TARGET_ATTACKED_OWNER:
                case OWNER_ATTACKED_TARGET:
                case CUSTOM:
                    break;
                default:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        if (!DisguiseAPI.isDisguised(event.getPlayer())) {
            return;
        }
        Location to = event.getTo();
        Location from = event.getFrom();
        if (DisguiseConfig.isBedPacketsEnabled()) {
            int x1 = (int) Math.floor(to.getX() / 16D) - 17;
            int x2 = (int) Math.floor(from.getX() / 16D) - 17;
            int z1 = (int) Math.floor(to.getZ() / 16D) - 17;
            int z2 = (int) Math.floor(from.getZ() / 16D) - 17;
            if (x1 - (x1 % 8) != x2 - (x2 % 8) || z1 - (z1 % 8) != z2 - (z2 % 8)) {
                chunkMove(event.getPlayer(), null, from);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!event.isCancelled()) {
                        chunkMove(event.getPlayer(), event.getTo(), null);
                    } else {
                        chunkMove(event.getPlayer(), event.getPlayer().getLocation(), null);
                    }
                });
            }
        }
        if (DisguiseConfig.isUndisguiseOnWorldChange() && to.getWorld() != null && from.getWorld() != null
                && to.getWorld() != from.getWorld()) {
            for (Disguise disguise : DisguiseAPI.getDisguises(event.getPlayer())) {
                disguise.removeDisguise();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && DisguiseAPI.isDisguised((Player) event.getEntered(), event.getEntered())) {
            DisguiseUtilities.removeSelfDisguise((Player) event.getEntered());
            ((Player) event.getEntered()).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleLeave(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            final Disguise disguise = DisguiseAPI.getDisguise((Player) event.getExited(), event.getExited());
            if (disguise != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    DisguiseUtilities.setupFakeDisguise(disguise);
                    ((Player) disguise.getEntity()).updateInventory();
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        if (!DisguiseAPI.isDisguised(event.getPlayer())) {
            return;
        }
        if (DisguiseConfig.isBedPacketsEnabled()) {
            chunkMove(event.getPlayer(), event.getPlayer().getLocation(), null);
        }
        if (DisguiseConfig.isUndisguiseOnWorldChange()) {
            for (Disguise disguise : DisguiseAPI.getDisguises(event.getPlayer())) {
                disguise.removeDisguise();
            }
        } else {
            final boolean viewSelfToggled = DisguiseAPI.isViewSelfToggled(event.getPlayer());
            if (viewSelfToggled) {
                final Disguise disguise = DisguiseAPI.getDisguise(event.getPlayer());
                disguise.setViewSelfDisguise(false);
                Bukkit.getScheduler().runTaskLater(plugin, () -> disguise.setViewSelfDisguise(true), 20L);
            }
        }
    }
}
