package com.thenexusreborn.survivalgames;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SGPlayer {
    
    //Player based fields
    private NexusPlayer nexusPlayer;
    private Player spigotPlayer;
    
    //Stats
    private SGPlayerStats stats;
    
    //Accessor fields for where a player is, these are mutally exclusive
    private Lobby lobby;
    private Game game;
    
    //Conveniance fields, similar to the Game and Lobby fields
    private LobbyPlayer lobbyPlayer;
    private GamePlayer gamePlayer;
    
    //Fields for things related to SG and the Player
    private long joinTime;

    public SGPlayer(NexusPlayer nexusPlayer, Player spigotPlayer) {
        this.nexusPlayer = nexusPlayer;
        this.spigotPlayer = spigotPlayer;
        this.joinTime = System.currentTimeMillis();
    }

    public SGPlayer(NexusPlayer nexusPlayer) {
        this(nexusPlayer, Bukkit.getPlayer(nexusPlayer.getUniqueId()));
    }

    public SGPlayer(Player spigotPlayer) {
        this(NexusAPI.getApi().getPlayerManager().getNexusPlayer(spigotPlayer.getUniqueId()), spigotPlayer);
    }
    
    public void sendMessage(String message) {
        getSpigotPlayer().sendMessage(StarColors.color(message));
    }
    
    public String getName() {
        return getNexusPlayer().getName();
    }

    public NexusPlayer getNexusPlayer() {
        return nexusPlayer;
    }

    public Player getSpigotPlayer() {
        return spigotPlayer;
    }

    public SGPlayerStats getStats() {
        return stats;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public Game getGame() {
        return game;
    }

    public LobbyPlayer getLobbyPlayer() {
        return lobbyPlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setStats(SGPlayerStats stats) {
        this.stats = stats;
    }

    public void setLobby(Lobby lobby, LobbyPlayer lobbyPlayer) {
        this.lobby = lobby;
        this.lobbyPlayer = lobbyPlayer;
        this.game = null;
        this.gamePlayer = null;
    }

    public void setGame(Game game, GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
        this.lobby = null;
        this.lobbyPlayer = null;
    }

    public long getJoinTime() {
        return this.joinTime;
    }

    public UUID getUniqueId() {
        return nexusPlayer.getUniqueId();
    }

    public String getColoredName() {
        return getNexusPlayer().getColoredName();
    }
}