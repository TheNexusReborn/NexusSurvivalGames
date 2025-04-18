package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class DeathmatchSetupThread extends StarThread<SurvivalGames> {
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

            if (game.getState() == Game.State.TELEPORT_DEATHMATCH_DONE) {
                game.startDeathmatchWarmup();
            }
        }
    }
}
