package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class DeathmatchSetupTask extends NexusThread<SurvivalGames> {
    public DeathmatchSetupTask(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
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
        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
            game.startDeathmatchWarmup();
        }
    }
}
