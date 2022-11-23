package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DamagersTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public DamagersTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Game game = plugin.getGame();
        
        if (game == null) {
            return;
        }
    
        for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
            Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
            if (player.getHealth() == player.getMaxHealth()) {
                gamePlayer.getDamageInfo().clearDamagers();
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 10L);
    }
}
