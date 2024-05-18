package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.scoreboard.game.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

import java.util.ArrayList;

public class PlayerScoreboardThread extends NexusThread<SurvivalGames> {
    public PlayerScoreboardThread(SurvivalGames plugin) {
        super(plugin, new ThreadOptions().repeating(true).period(1L));
    }
    
    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
                NexusScoreboard scoreboard = gamePlayer.getScoreboard();
                ScoreboardView view = scoreboard.getView();
                if (gamePlayer.getCombatTag().isInCombat()) {
                    if (!(view instanceof CombatTagBoard)) {
                        scoreboard.setView(new CombatTagBoard(scoreboard, plugin));
                    }
                } else if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                    if (!(view instanceof MutationBoard)) {
                        scoreboard.setView(new MutationBoard(scoreboard, plugin));
                    }
                } else if (!game.isDebug()) {
                    if (!(view instanceof GameBoard)) {
                        scoreboard.setView(new GameBoard(scoreboard, plugin));
                    }
                }
                //TODO Debug Boards for Game
            }
        }
    }
}
