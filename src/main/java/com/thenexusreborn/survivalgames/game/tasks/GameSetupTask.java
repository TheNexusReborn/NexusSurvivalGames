package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.scheduler.BukkitRunnable;

public class GameSetupTask extends BukkitRunnable {
    private SurvivalGames plugin;
    
    public GameSetupTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        if (plugin.getGame() == null) {
            return;
        }
        
        if (Game.getControlType() == ControlType.MANUAL) {
            return;
        }
        
        Game game = plugin.getGame();
        if (game.getState() == GameState.SETUP_COMPLETE) {
            game.assignStartingTeams();
        } else if (game.getState() == GameState.TEAMS_ASSIGNED) {
            game.teleportStart();
        } else if (game.getState() == GameState.TELEPORT_START_DONE) {
            game.startWarmup();
        }
    }
}
