package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CombatTagTask extends BukkitRunnable {
    private SurvivalGames plugin;
    
    public CombatTagTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
        
        for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                continue;
            }
            
            CombatTag combatTag = gamePlayer.getCombatTag();
            
            if (combatTag.getOther() == null) {
                continue;
            }
            
            if (combatTag.getTimestamp() + 5000 > System.currentTimeMillis()) {
                continue;
            }
            
            combatTag.setOther(null);
            gamePlayer.sendMessage("&6&l>> &eYou are no longer in combat.");
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 5L);
    }
}
