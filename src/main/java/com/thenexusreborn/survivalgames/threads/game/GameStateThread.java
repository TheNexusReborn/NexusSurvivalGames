package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class GameStateThread extends NexusThread<SurvivalGames> {
    public GameStateThread(SurvivalGames plugin) {
        super(plugin, 1L, false);
    }

    @Override
    public void onRun() {
        for (Game game : plugin.getGames()) {
            if (game.getControlType() == ControlType.MANUAL) {
                continue;
            }

            if (game.getState() == GameState.SETUP_COMPLETE) {
                game.assignStartingTeams();
            } else if (game.getState() == GameState.TEAMS_ASSIGNED) {
                game.teleportStart();
            } else if (game.getState() == GameState.TELEPORT_START_DONE) {
                game.startWarmup();
            } else if (game.getState() == GameState.WARMUP_DONE) {
                game.startGame();
            } else if (game.getState() == GameState.INGAME_DONE) {
                game.teleportDeathmatch();
            } else if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                game.teleportDeathmatch();
            } else if (game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
                game.startDeathmatch();
            } else if (game.getState() == GameState.GAME_COMPLETE) {
                game.end();
            } else if (game.getState() == GameState.NEXT_GAME_READY) {
                game.nextGame();
            }
        }
    }
}