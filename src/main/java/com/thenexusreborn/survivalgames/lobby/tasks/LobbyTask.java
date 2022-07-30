package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyTask extends BukkitRunnable {
    
    private final SurvivalGames plugin;
    
    public LobbyTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        if (plugin.getGame() != null) {
            return;
        }
        World world = Bukkit.getWorld(ServerProperties.getLevelName());
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
        
        Tournament tournament = NexusAPI.getApi().getTournament();
        Lobby lobby = plugin.getLobby();
        if (tournament != null && tournament.isActive()) {
            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                if (!lobby.getLobbySettings().getType().equals("tournament")) {
                    LobbySettings settings = plugin.getLobbySettings("tournament");
                    if (settings == null) {
                        settings = plugin.getLobbySettings("default");
                    }
                    lobby.setLobbySettings(settings);
                }
                
                if (!lobby.getGameSettings().getType().equals("tournament")) {
                    GameSettings settings = plugin.getGameSettings("tournament");
                    if (settings == null) {
                        settings = plugin.getGameSettings("default");
                    }
                    lobby.setGameSettings(settings);
                }
            }
        } else {
            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                if (lobby.getLobbySettings().getType().equals("tournament")) {
                    lobby.setLobbySettings(plugin.getLobbySettings("default"));
                }
    
                if (lobby.getGameSettings().getType().equals("tournament")) {
                    lobby.setGameSettings(plugin.getGameSettings("default"));
                }
            }
        }
        
        
        world.setThundering(false);
        world.setStorm(false);
        
        world.setGameRuleValue("doFireSpread", "false");
        
        if (lobby.getState() != LobbyState.MAP_EDITING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().getBlockY() < lobby.getSpawnpoint().getBlockY() - 20) {
                    player.teleport(lobby.getSpawnpoint());
                }
            }
        }
        
        for (SpigotNexusPlayer player : lobby.getPlayers()) {
            for (SpigotNexusPlayer other : lobby.getPlayers()) {
                if (player.getPreferences().get("vanish").getValue()) {
                    if (other.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        other.getPlayer().hidePlayer(player.getPlayer());
                    }
                } else {
                    other.getPlayer().showPlayer(player.getPlayer());
                }
                
                if (other.getPreferences().get("vanish").getValue()) {
                    if (player.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        player.getPlayer().hidePlayer(other.getPlayer());
                    }
                } else {
                    player.getPlayer().showPlayer(other.getPlayer());
                }
            }
        }
        
        boolean resetLobby = false;
        if (lobby.getState() != LobbyState.WAITING) {
            if (lobby.getTimer() == null) {
                resetLobby = true;
            } else if (lobby.getTimer().getSecondsLeft() <= 0) {
                resetLobby = true;
            }
            
            if (Bukkit.getOnlinePlayers().size() <= 1) {
                resetLobby = true;
            }
        }
        
        if (resetLobby) {
            lobby.resetInvalidState();
        }
    }
}
