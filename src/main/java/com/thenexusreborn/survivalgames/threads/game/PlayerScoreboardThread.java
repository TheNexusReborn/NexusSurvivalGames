package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.scoreboard.game.CombatTagBoard;
import com.thenexusreborn.survivalgames.scoreboard.game.GameBoard;
import com.thenexusreborn.survivalgames.scoreboard.game.MutationBoard;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class PlayerScoreboardThread extends StarThread<SurvivalGames> {
    public PlayerScoreboardThread(SurvivalGames plugin) {
        super(plugin, "", new ThreadOptions().repeating(true).period(1L));
    }
    
    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {

            Set<UUID> players = server.getPlayers();

            for (UUID player : players) {
                SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player);
                if (sgPlayer == null) {
                    continue;
                }

                Game game = sgPlayer.getGame();
                if (game == null) {
                    continue;
                }

                
            }

            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            if (game.getState() == GameState.ENDED) {
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
