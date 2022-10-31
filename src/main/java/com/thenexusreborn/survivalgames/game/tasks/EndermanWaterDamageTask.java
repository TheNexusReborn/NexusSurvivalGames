package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EndermanWaterDamageTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public EndermanWaterDamageTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        if (plugin.getGame() == null) {
            return;
        }
        
        if (!(plugin.getGame().getState() == GameState.INGAME || plugin.getGame().getState() == GameState.INGAME_DEATHMATCH)) {
            return;
        }
    
        for (GamePlayer gamePlayer : new ArrayList<>(plugin.getGame().getPlayers().values())) {
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                continue;
            }
            
            if (!gamePlayer.getMutation().getType().getId().equals("enderman")) {
                continue;
            }
    
            Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                player.damage(1);
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 20L);
    }
}
