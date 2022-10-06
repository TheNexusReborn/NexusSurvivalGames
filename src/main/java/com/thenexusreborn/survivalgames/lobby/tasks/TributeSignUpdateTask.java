package com.thenexusreborn.survivalgames.lobby.tasks;

import com.mojang.authlib.GameProfile;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

public class TributeSignUpdateTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    private UUID randomUUID = UUID.randomUUID();
    
    public TributeSignUpdateTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Lobby lobby = plugin.getLobby();
        if (lobby == null) {
            return;
        }
    
        if (plugin.getGame() != null) {
            return;
        }
    
        if (lobby.getState() == LobbyState.MAP_EDITING) {
            return;
        }
    
        Set<TributeSign> tributeSigns = new TreeSet<>(lobby.getTributeSigns());
        if (tributeSigns.size() < 1) {
            return;
        }
    
        List<NexusPlayer> players = new ArrayList<>(lobby.getPlayers());
    
        for (TributeSign tributeSign : tributeSigns) {
            if (tributeSign.getHeadLocation() == null) {
                continue;
            }
            if (tributeSign.getSignLocation() == null) {
                continue;
            }
            Skull skull = (Skull) tributeSign.getHeadLocation().getBlock().getState();
            if (players.size() <= tributeSign.getIndex()) {
                try {
                    Field profileField = skull.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skull, new GameProfile(randomUUID, ""));
                    skull.update();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                String[] lines = new String[] {"", "", "", ""};
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld() == tributeSign.getSignLocation().getWorld()) {
                        p.sendSignChange(tributeSign.getSignLocation(), lines);
                    }
                }
                continue;
            }
            
            NexusPlayer nexusPlayer = players.get(tributeSign.getIndex());
            Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
            String name;
            if (nexusPlayer.getName().length() <= 14) {
                name = MCUtils.color(nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName());
            } else {
                name = nexusPlayer.getName();
            }
            int score = (int) nexusPlayer.getStats().getValue("sg_score");
            int kills = (int) nexusPlayer.getStats().getValue("sg_kills");
            int wins = (int) nexusPlayer.getStats().getValue("sg_wins");
            skull.setOwner(player.getName());
            skull.update();
    
            String[] lines = new String[] {name, "Score: " + score + "", "Kills: " + kills + "", "Wins: " + wins + ""};
    
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.getWorld() == tributeSign.getSignLocation().getWorld()) {
                    op.sendSignChange(tributeSign.getSignLocation(), lines);
                }
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 20L);
    }
}
