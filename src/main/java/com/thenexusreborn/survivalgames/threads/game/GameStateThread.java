package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
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
            
            if (game.getControlType() != ControlType.AUTO) {
                continue;
            }
            
            switch (game.getState()) {
                case SETUP_COMPLETE -> game.assignStartingTeams();
                case TEAMS_ASSIGNED -> game.teleportStart();
                case TELEPORT_START_DONE -> game.startWarmup();
                case WARMUP_DONE -> game.startGame();
                case TELEPORT_DEATHMATCH_DONE -> game.startDeathmatchWarmup();
                case DEATHMATCH_WARMUP_DONE -> game.startDeathmatch();
                case GAME_COMPLETE -> game.end();
                case NEXT_GAME_READY -> game.nextGame();
                default -> {}
            }
        }
    }
}