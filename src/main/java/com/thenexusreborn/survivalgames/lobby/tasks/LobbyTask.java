package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
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
        if (tournament != null && tournament.isActive()) {
            if (plugin.getLobby().getState() == LobbyState.WAITING || plugin.getLobby().getState() == LobbyState.COUNTDOWN) {
                if (!plugin.getLobby().getLobbySettings().getType().equals("tournament")) {
                    LobbySettings settings = plugin.getLobbySettings("tournament");
                    if (settings == null) {
                        settings = plugin.getLobbySettings("default");
                    }
                    plugin.getLobby().setLobbySettings(settings);
                }
                
                if (!plugin.getLobby().getGameSettings().getType().equals("tournament")) {
                    GameSettings settings = plugin.getGameSettings("tournament");
                    if (settings == null) {
                        settings = plugin.getGameSettings("default");
                    }
                    plugin.getLobby().setGameSettings(settings);
                }
            }
        } else {
            if (plugin.getLobby().getState() == LobbyState.WAITING || plugin.getLobby().getState() == LobbyState.COUNTDOWN) {
                if (plugin.getLobby().getLobbySettings().getType().equals("tournament")) {
                    plugin.getLobby().setLobbySettings(plugin.getLobbySettings("default"));
                }
    
                if (plugin.getLobby().getGameSettings().getType().equals("tournament")) {
                    plugin.getLobby().setGameSettings(plugin.getGameSettings("default"));
                }
            }
        }
        
        
        world.setThundering(false);
        world.setStorm(false);
        
        world.setGameRuleValue("doFireSpread", "false");
        
        if (plugin.getLobby().getState() != LobbyState.MAP_EDITING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().getBlockY() < plugin.getLobby().getSpawnpoint().getBlockY() - 20) {
                    player.teleport(plugin.getLobby().getSpawnpoint());
                }
            }
        }
        
        for (SpigotNexusPlayer player : plugin.getLobby().getPlayers()) {
            for (SpigotNexusPlayer other : plugin.getLobby().getPlayers()) {
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
    }
}
