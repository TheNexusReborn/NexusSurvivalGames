package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorUpdateTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public SpectatorUpdateTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Game game = plugin.getGame();
        if (game != null) {
            for (GamePlayer player : game.getPlayers().values()) {
                if (player.getTeam() != GameTeam.TRIBUTES) {
                    SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                }
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 1L, 20L);
    }
}
