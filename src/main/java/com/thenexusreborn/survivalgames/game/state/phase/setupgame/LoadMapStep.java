package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;

public class LoadMapStep extends GamePhaseStep {
    public LoadMapStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "load_map", prerequisiteSteps);
    }
    
    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        if (!game.getGameMap().load(Game.getPlugin())) {
            setStatus(StepStatus.ERROR);
            return false;
        }
        
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
