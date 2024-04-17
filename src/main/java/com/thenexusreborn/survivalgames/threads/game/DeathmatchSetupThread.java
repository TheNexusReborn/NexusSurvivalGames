package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class DeathmatchSetupThread extends NexusThread<SurvivalGames> {
    public DeathmatchSetupThread(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
    }

    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }

            if (game.getControlType() == ControlType.MANUAL) {
                continue;
            }

            if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                game.startDeathmatchWarmup();
            }
        }
    }
}
