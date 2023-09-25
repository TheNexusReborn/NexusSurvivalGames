package com.thenexusreborn.survivalgames.threads.lobby;

import com.mojang.authlib.GameProfile;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class TributeSignUpdateThread extends NexusThread<SurvivalGames> {
    
    private UUID randomUUID = UUID.randomUUID();
    
    public TributeSignUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
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
        if (tributeSigns.isEmpty()) {
            return;
        }
    
        List<LobbyPlayer> players = lobby.getPlayers();
    
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
                String[] lines = {"", "", "", ""};
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld() == tributeSign.getSignLocation().getWorld()) {
                        p.sendSignChange(tributeSign.getSignLocation(), lines);
                    }
                }
                continue;
            }
            
            LobbyPlayer lobbyPlayer = players.get(tributeSign.getIndex());
            Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
            String name;
            if (lobbyPlayer.getName().length() <= 14) {
                name = MCUtils.color(lobbyPlayer.getRank().getColor() + lobbyPlayer.getName());
            } else {
                name = lobbyPlayer.getName();
            }
            int score = lobbyPlayer.getStatValue("sg_score").getAsInt();
            int kills = lobbyPlayer.getStatValue("sg_kills").getAsInt();
            int wins = lobbyPlayer.getStatValue("sg_wins").getAsInt();
            skull.setOwner(player.getName());
            skull.update();
    
            String[] lines = {name, "Score: " + score + "", "Kills: " + kills + "", "Wins: " + wins + ""};
    
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.getWorld() == tributeSign.getSignLocation().getWorld()) {
                    op.sendSignChange(tributeSign.getSignLocation(), lines);
                }
            }
        }
    }
}
