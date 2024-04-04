package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyType;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SGVirtualServer extends VirtualServer {

    private SurvivalGames plugin;

    private Lobby lobby;
    private Game game;

    public SGVirtualServer(SurvivalGames plugin, InstanceServer parent, String name) {
        super(parent, name, "survivalgames", 24);
        this.plugin = plugin;
    }

    public SGVirtualServer(SurvivalGames plugin, String name) {
        super(name, "survivalgames", 24);
        this.plugin = plugin;
    }

    @Override
    public boolean recalculateVisibility(UUID playerUUID, UUID otherPlayerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        Player otherPlayer = Bukkit.getPlayer(otherPlayerUUID);
        
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(playerUUID);
        NexusPlayer otherNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(otherPlayerUUID);
        
        Rank playerRank = nexusPlayer.getRank();
        Rank otherPlayerRank = otherNexusPlayer.getRank();
        
        boolean playerIsVanished = playerRank.ordinal() <= Rank.HELPER.ordinal() && nexusPlayer.getToggleValue("vanish");
        boolean otherPlayerIsVanished = otherPlayerRank.ordinal() <= Rank.HELPER.ordinal() && otherNexusPlayer.getToggleValue("vanish");
        
        if (game == null) {
            if (playerIsVanished && otherPlayerIsVanished) {
                if (playerRank.ordinal() >= otherPlayerRank.ordinal()) {
                    player.showPlayer(otherPlayer);
                    otherPlayer.showPlayer(player);
                } else {
                    otherPlayer.hidePlayer(player);
                    player.showPlayer(otherPlayer);
                }
            } else if (playerIsVanished && !otherPlayerIsVanished) {
                otherPlayer.hidePlayer(player);
                player.showPlayer(otherPlayer);
            } else if (otherPlayerIsVanished && !playerIsVanished) {
                player.hidePlayer(otherPlayer);
                otherPlayer.showPlayer(player);
            }
        } else {
            if (game.getState() == GameState.ENDING) {
                player.showPlayer(otherPlayer);
                otherPlayer.showPlayer(player);
                return true;
            }
            
            GamePlayer gamePlayer = game.getPlayer(playerUUID);
            GamePlayer otherGamePlayer = game.getPlayer(otherPlayerUUID);
            
            boolean playerIsSpectator = gamePlayer.getTeam() == GameTeam.SPECTATORS;
            boolean otherPlayerIsSpectator = otherGamePlayer.getTeam() == GameTeam.SPECTATORS;
            
            if (playerIsSpectator && otherPlayerIsSpectator) {
                player.hidePlayer(otherPlayer);
                otherPlayer.hidePlayer(player);
            } else if (playerIsSpectator && !otherPlayerIsSpectator) {
                player.showPlayer(otherPlayer);
                otherPlayer.hidePlayer(player);
            } else {
                player.showPlayer(otherPlayer);
                otherPlayer.showPlayer(player);
            }
        }
        
        return true;
    }

    @Override
    public void join(NexusPlayer nexusPlayer) {
        if (game == null) {
            if (lobby.getPlayingCount() >= lobby.getLobbySettings().getMaxPlayers()) {
                boolean isStaff = nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal();
                boolean isInVanish = nexusPlayer.getToggleValue("vanish");
                if (!(isStaff && isInVanish)) {
                    nexusPlayer.sendMessage("&cThe lobby is full.");
                    return;
                }
            }
        }
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        SGPlayerStats stats = sgPlayer.getStats();
        if (game != null) {
            GameState state = game.getState();
            if (state == GameState.ASSIGN_TEAMS) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (game.getState() != GameState.ASSIGN_TEAMS) {
                            game.addPlayer(nexusPlayer, stats);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 1L, 1L);
            } else {
                game.addPlayer(nexusPlayer, stats);
            }
        } else {
            lobby.addPlayer(sgPlayer);
        }
        
        this.players.add(nexusPlayer.getUniqueId());
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        
        if (sgPlayer.getGame() != null) {
            sgPlayer.getGame().removePlayer(nexusPlayer);
        }
        
        if (sgPlayer.getLobby() != null) {
            sgPlayer.getLobby().removePlayer(nexusPlayer);
        }
        
        this.players.remove(nexusPlayer.getUniqueId());
    }

    @Override
    public void onStart() {
        this.lobby = new Lobby(plugin, this, LobbyType.CUSTOM);
        this.lobby.setup();
    }

    @Override
    public void onStop() {
        if (this.lobby != null) {
            this.lobby.handleShutdown();
        }

        if (this.game != null) {
            this.game.handleShutdown();
        }
    }

    public Game getGame() {
        return game;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}