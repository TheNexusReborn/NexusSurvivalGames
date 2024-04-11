package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;

public class SetupSpawnsStep extends GamePhaseStep {
    public SetupSpawnsStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "setup_spawns", prerequisiteSteps);
    }

    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        try {
            for (int i = 0; i < game.getGameMap().getSpawns().size(); i++) {
                game.setSpawn(i, null);
            }
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
            e.printStackTrace();
        }
        
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
