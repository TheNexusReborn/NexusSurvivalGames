package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class GameStateThread extends NexusThread<SurvivalGames> {
    public GameStateThread(SurvivalGames plugin) {
        super(plugin, 1L, false);
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

            if (game.getGameState().progress()) {
                continue; //Progresses the game based on currently implemented states. Otherwise, default to the old state system.
            }

            if (game.getState() == OldGameState.WARMUP_DONE) {
                game.startGame();
            } else if (game.getState() == OldGameState.INGAME_DONE) {
                game.teleportDeathmatch();
            } else if (game.getState() == OldGameState.TELEPORT_DEATHMATCH_DONE) {
                game.teleportDeathmatch();
            } else if (game.getState() == OldGameState.DEATHMATCH_WARMUP_DONE) {
                game.startDeathmatch();
            } else if (game.getState() == OldGameState.GAME_COMPLETE) {
                game.end();
            } else if (game.getState() == OldGameState.NEXT_GAME_READY) {
                game.nextGame();
            }
        }
    }
}