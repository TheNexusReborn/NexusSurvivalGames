package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyType;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.scheduler.BukkitRunnable;

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