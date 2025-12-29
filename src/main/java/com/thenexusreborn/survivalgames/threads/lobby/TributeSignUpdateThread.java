package com.thenexusreborn.survivalgames.threads.lobby;

import com.mojang.authlib.GameProfile;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.lobby.TributeSign;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TributeSignUpdateThread extends StarThread<SurvivalGames> {
    
    private UUID randomUUID = UUID.randomUUID();
    
    public TributeSignUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers().values()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            if (lobby.getState() == LobbyState.MAP_CONFIGURATING) {
                continue;
            }

            Set<TributeSign> tributeSigns = new TreeSet<>(lobby.getTributeSigns());
            if (tributeSigns.isEmpty()) {
                continue;
            }

            List<LobbyPlayer> players = lobby.getPlayers();
            
            players.removeIf(p -> p.getToggleValue("vanish") || p.isSpectating());

            for (TributeSign tributeSign : tributeSigns) {
                if (tributeSign.getHeadLocation() == null) {
                    continue;
                }
                if (tributeSign.getSignLocation() == null) {
                    continue;
                }
                Skull skull = (Skull) tributeSign.getHeadLocation().getBlock().getState();
                if (players.isEmpty() || players.size() <= tributeSign.getIndex()) {
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
                        if (p.getWorld().getName().equalsIgnoreCase(tributeSign.getSignLocation().getWorld().getName())) {
                            p.sendSignChange(tributeSign.getSignLocation(), lines);
                        }
                    }
                    continue;
                }

                LobbyPlayer lobbyPlayer = players.get(tributeSign.getIndex());
                Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
                
                if (player == null) {
                    continue;
                }
                
                String name;
                if (lobbyPlayer.getName().length() <= 14) {
                    name = StarColors.color(lobbyPlayer.getEffectiveRank().getColor() + lobbyPlayer.getName());
                } else {
                    name = lobbyPlayer.getName();
                }
                int score = lobbyPlayer.getStats().getScore();
                int kills = lobbyPlayer.getStats().getKills();
                int wins = lobbyPlayer.getStats().getWins();
                skull.setOwner(player.getName());
                skull.update();

                String[] lines = {name, "Score: " + score, "Kills: " + kills, "Wins: " + wins};

                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (op.getWorld().getName().equalsIgnoreCase(tributeSign.getSignLocation().getWorld().getName())) {
                        op.sendSignChange(tributeSign.getSignLocation(), lines);
                    }
                }
            }
        }
    }
}
