package com.thenexusreborn.survivalgames.game.state;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.Map;
import java.util.TreeMap;

public abstract class GamePhase {
    protected Game game;
    protected String name;
    protected PhaseStatus status;
    
    //A status is only considered completed when it is replaced by a new status. The setStatus method handles this automatically.
    protected Map<Long, PhaseStatus> completedStatuses = new TreeMap<>();
    
    public GamePhase(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    /**
     * Called before the phase begins to check and/or setup the necessary things needed for the phase
     */
    public void prephase() {
        
    }

    /**
     * The main function of phase, this is where the the processing of the phase is started and the status is updated here.
     */
    public abstract void beginphase();

    /**
     * 
     */
    public void postphase() {
        
    }

    public PhaseStatus getStatus() {
        return status;
    }

    protected void setStatus(PhaseStatus status) {
        this.completedStatuses.put(System.currentTimeMillis(), this.status);
        this.status = status;
    }

    @Override
    public String toString() {
        return "GamePhase{" +
                "name='" + name + '\'' +
                ", status=" + status.toString() +
                '}';
    }
}