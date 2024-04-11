package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;

public class CopyMapStep extends GamePhaseStep {
    public CopyMapStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "copy_map", prerequisiteSteps);
    }
    
    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        if (!game.getGameMap().copyFolder(Game.getPlugin(), game.getServer().getName() + "-", false)) {
            setStatus(StepStatus.ERROR);
            return false;
        }
        
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
