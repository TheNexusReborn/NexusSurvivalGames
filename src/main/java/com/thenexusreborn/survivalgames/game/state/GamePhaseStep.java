package com.thenexusreborn.survivalgames.game.state;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GamePhaseStep {
    protected Game game;
    protected GamePhase gamePhase;
    protected StepStatus status;
    protected String name;
    protected List<GamePhaseStep> prerequisiteSteps = new ArrayList<>();

    protected Map<StepStatus, Long> completedStatuses = new HashMap<>();

    public GamePhaseStep(GamePhase gamePhase, String name, GamePhaseStep... prerequisiteSteps) {
        this.game = gamePhase.getGame();
        this.gamePhase = gamePhase;
        this.name = name;
        this.prerequisiteSteps.addAll(List.of(prerequisiteSteps));
    }
    
    public boolean requirementsMet() {
        if (gamePhase.checkPlayerCount()) {
            return false;
        }
        
        for (GamePhaseStep ps : this.prerequisiteSteps) {
            if (!ps.isCompleted()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean tryMeetRequirements() {
        if (gamePhase.checkPlayerCount()) {
            return false;
        }
        
        for (GamePhaseStep ps : this.prerequisiteSteps) {
            if (!ps.isCompleted()) {
                if (!ps.requirementsMet()) {
                    return false;
                }

                if (!ps.setup() && !ps.run()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean setup() {
        return true;
    }
    
    public boolean run() {
        return true;
    }
    
    public boolean cleanup() {
        return true;
    }

    public void setStatus(StepStatus status) {
        this.completedStatuses.put(status, System.currentTimeMillis());
        this.status = status;
    }

    public Game getGame() {
        return game;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public StepStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
    
    public boolean isCompleted() {
        if (this.status == null) {
            return false;
        }
        
        return this.status.equals(StepStatus.COMPLETE);
    }
}
