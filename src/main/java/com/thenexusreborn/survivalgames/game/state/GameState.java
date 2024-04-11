package com.thenexusreborn.survivalgames.game.state;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.LinkedList;

public class GameState {
    private final Game game;
    private GamePhase lastPhase; //The last phase before the current phase.
    private GamePhase currentPhase; //Current Phase
    private GamePhase nextPhase; //Will be determined by mode logic or by commands
    private LinkedList<GamePhase> pastPhases = new LinkedList<>(); //Storage of phases

    public GameState(Game game) {
        this.game = game;
    }
    
    //Steps to the next thing, either the next step in a phase, or the next phase
    //Returning true means it was a success
    //Returning false means that it was not, most likely due to the previous not complete
    public boolean step() {
        if (currentPhase != null) {
            if (!currentPhase.step()) {
                if (!currentPhase.isComplete()) {
                    return false; //Phase failed to step, and is not complete, this failed to step forward as well.
                }
            }
            
            if (currentPhase.isComplete()) {
                return goToNextPhase();
            }
        }
        
        return goToNextPhase();
    }
    
    private boolean goToNextPhase() {
        this.lastPhase = currentPhase;
        if (nextPhase == null) {
            nextPhase = game.determineNextPhase(currentPhase);

            if (nextPhase == null) {
                return false; //Next phase is null, this means that we go to old way of checking things
            }
        }

        this.currentPhase.cleanup();
        this.setCurrentPhase(nextPhase);
        this.nextPhase = game.determineNextPhase(currentPhase);
        if (!this.currentPhase.requirementsMet()) {
            if (!this.currentPhase.tryMeetRequirements()) {
                return false; //Phase did not meet requirements and could not make the requirements be met
            }
        }
        
        if (!this.currentPhase.setup()) {
            
            return false;
        }
        
        if (!this.currentPhase.run()) {
            return false;
        }

        return this.currentPhase.cleanup();
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public GamePhase getNextPhase() {
        return nextPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        pastPhases.add(currentPhase);
        lastPhase = currentPhase;
        this.currentPhase = currentPhase;
    }

    public LinkedList<GamePhase> getPastPhases() {
        return new LinkedList<>(pastPhases);
    }

    public GamePhase getLastPhase() {
        return lastPhase;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "lastPhase=" + lastPhase +
                ", currentPhase=" + currentPhase +
                ", nextPhase=" + nextPhase +
                ", pastPhases=" + pastPhases +
                '}';
    }
}
