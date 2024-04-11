package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;
import com.thenexusreborn.survivalgames.scoreboard.GameTablistHandler;
import com.thenexusreborn.survivalgames.scoreboard.game.GameBoard;

public class SetScoreboardsStep extends GamePhaseStep {
    public SetScoreboardsStep(GamePhase gamePhase) {
        super(gamePhase, "assign_scoreboards");
    }

    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        try {
            for (GamePlayer player : game.getPlayers().values()) {
                NexusScoreboard scoreboard = player.getScoreboard();
                scoreboard.setView(new GameBoard(scoreboard, Game.getPlugin()));
                scoreboard.setTablistHandler(new GameTablistHandler(scoreboard, Game.getPlugin()));
            }
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
            e.printStackTrace();
            return false;
        }
        setStatus(StepStatus.COMPLETE);
        return true;
    }

    @Override
    public boolean cleanup() {
        return super.cleanup();
    }
}
