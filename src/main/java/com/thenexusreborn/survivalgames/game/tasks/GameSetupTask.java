package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class GameSetupTask extends NexusThread<SurvivalGames> {
    public GameSetupTask(SurvivalGames plugin) {
        super(plugin, 1L, false);
    }
    
    @Override
    public void onRun() {
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
