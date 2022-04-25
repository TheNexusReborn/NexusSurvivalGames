package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.SurvivalGames;
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
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getBlockY() < plugin.getLobby().getSpawnpoint().getBlockX() - 20) {
                player.teleport(plugin.getLobby().getSpawnpoint());
            }
        }
    }
}
