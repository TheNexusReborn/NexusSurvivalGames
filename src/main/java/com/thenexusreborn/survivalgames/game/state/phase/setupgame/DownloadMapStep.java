package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;

public class DownloadMapStep extends GamePhaseStep {
    public DownloadMapStep(GamePhase gamePhase) {
        super(gamePhase, "download_map");
    }

    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        if (getGame().getGameMap().download(Game.getPlugin())) {
            setStatus(StepStatus.COMPLETE);
            return true;
        }
        
        setStatus(StepStatus.ERROR);
        return false;
    }
}
