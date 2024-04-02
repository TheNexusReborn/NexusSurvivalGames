package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class DeathmatchSetupThread extends NexusThread<SurvivalGames> {
    public DeathmatchSetupThread(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
    }
    
    @Override
    public void onRun() {
        for (Game game : plugin.getGames()) {
            if (game.getControlType() == ControlType.MANUAL) {
                continue;
            }

            if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                game.startDeathmatchWarmup();
            }
        }
    }
}
