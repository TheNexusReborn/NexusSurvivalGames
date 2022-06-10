package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyWorldChecker extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public LobbyWorldChecker(SurvivalGames plugin) {
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
