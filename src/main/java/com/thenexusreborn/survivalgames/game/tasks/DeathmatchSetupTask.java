package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchSetupTask extends BukkitRunnable {
    private SurvivalGames plugin;
    
    public DeathmatchSetupTask(SurvivalGames plugin) {
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
        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
            game.startDeathmatchWarmup();
        }
    }
}
