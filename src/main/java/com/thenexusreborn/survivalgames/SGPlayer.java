package com.thenexusreborn.survivalgames;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.util.NickSGPlayerStats;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public class SGPlayer {
    
    //Player based fields
    private NexusPlayer nexusPlayer;
    private Player spigotPlayer;
    
    //Stats
    private SGPlayerStats stats;
    private NickSGPlayerStats nickSGPlayerStats;
    
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
        this(NexusReborn.getPlayerManager().getNexusPlayer(spigotPlayer.getUniqueId()), spigotPlayer);
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
        if (this.spigotPlayer == null) {
            this.spigotPlayer = Bukkit.getPlayer(getUniqueId());
        } else if (!this.spigotPlayer.isOnline()) {
            this.spigotPlayer = null;
        }
        
        return spigotPlayer;
    }
    
    public void playSound(Sound sound) {
        Player player = getSpigotPlayer();
        if (player == null) {
            return;
        }
        
        player.playSound(player.getLocation(), sound, 0.5F, 1F);
    }
    
    public SGPlayerStats getTrueStats() {
        return this.stats;
    }
    
    public NickSGPlayerStats getNickSGPlayerStats() {
        return nickSGPlayerStats;
    }
    
    public SGPlayerStats getStats() {
        if (getNexusPlayer().isNicked()) {
            if (this.nickSGPlayerStats == null) {
                this.nickSGPlayerStats = new NickSGPlayerStats(getUniqueId(), this.stats, getNexusPlayer().getNickname().isPersist());
            }
            
            return this.nickSGPlayerStats;
        }
        
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
    
    public void setNickSGPlayerStats(NickSGPlayerStats nickSGPlayerStats) {
        this.nickSGPlayerStats = nickSGPlayerStats;
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
    
    public String getTrueName() {
        return getNexusPlayer().getTrueName();
    }
    
    public String getTrueColoredName() {
        return getNexusPlayer().getTrueColoredName();
    }
    
    public ItemStack getItemInHand() {
        Player player = getSpigotPlayer();
        if (player != null) {
            return player.getItemInHand();
        }
        
        return null;
    }
    
    public boolean isBlocking() {
        Player player = getSpigotPlayer();
        if (player != null) {
            return player.isBlocking();
        }
        
        return false;
    }
    
    public void launchProjectile(Class<? extends Projectile> projectileClass) {
        Player player = getSpigotPlayer();
        if (player != null) {
            player.launchProjectile(projectileClass);
        }
    }
    
    public void setLevel(int level) {
        Player player = getSpigotPlayer();
        if (player != null) {
            player.setLevel(level);
        }
    }
    
    public Location getLocation() {
        Player player = getSpigotPlayer();
        if (player != null) {
            return player.getLocation();
        }
        return null;
    }
    
    public Vector getVelocity() {
        Player player = getSpigotPlayer();
        if (player != null) {
            return player.getVelocity();
        }
        
        return null;
    }
    
    public void setVelocity(Vector vector) {
        Player player = getSpigotPlayer();
        if (player != null) {
            player.setVelocity(vector);
        }
    }
    
    public boolean isOnline() {
        Player player = getSpigotPlayer();
        if (player != null) {
            return player.isOnline();
        }
        
        return false;
    }
    
    public void setAllowFlight(boolean allowFlight) {
        Player player = getSpigotPlayer();
        if (player != null) {
            player.setAllowFlight(true);
        }
    }
    
    public void showPlayer(SGPlayer sgPlayer) {
        Player player = getSpigotPlayer();
        if (player != null) {
            Player other = sgPlayer.getSpigotPlayer();
            if (other != null) {
                player.showPlayer(other);
            }
        }
    }
    
    public void hidePlayer(SGPlayer sgPlayer) {
        Player player = getSpigotPlayer();
        if (player != null) {
            Player other = sgPlayer.getSpigotPlayer();
            if (other != null) {
                player.hidePlayer(other);
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        SGPlayer sgPlayer = (SGPlayer) o;
        return Objects.equals(nexusPlayer, sgPlayer.nexusPlayer);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(nexusPlayer);
    }
}