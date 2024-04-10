package com.thenexusreborn.survivalgames.game.state;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.LinkedList;

/**
 * For the new way of handling how the game progression is tracked
 */
public class GameState {
    private final Game game;
    private GamePhase currentPhase; //Current Phase
    private GamePhase nextPhase; //Will be determined by mode logic or by commands
    private LinkedList<GamePhase> pastPhases = new LinkedList<>(); //Storage of phases

    public GameState(Game game) {
        this.game = game;
    }
    
    public boolean progress() {
        if (currentPhase != null) {
            if (!currentPhase.isComplete()) {
                return false;
            }
            
            currentPhase.postphase();
        }
        
        if (nextPhase == null) {
            return false; //The determination of the old system is based on the GameStateThread now and the return of this method
        }
        
        setCurrentPhase(nextPhase);
        nextPhase.prephase();
        nextPhase.beginphase();
        this.nextPhase = game.determineNextPhase(currentPhase);
        return true;
    }
    
    public boolean forceProgress() {
        if (currentPhase != null) {
            currentPhase.postphase();
        }

        if (nextPhase == null) {
            return false; //The determination of the old system is based on the GameStateThread now and the return of this method
        }

        setCurrentPhase(nextPhase);
        nextPhase.prephase();
        nextPhase.beginphase();
        this.nextPhase = game.determineNextPhase(currentPhase);
        return true;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public GamePhase getNextPhase() {
        return nextPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        pastPhases.add(currentPhase);
        this.currentPhase = currentPhase;
    }

    public void setNextPhase(GamePhase nextPhase) {
        this.nextPhase = nextPhase;
    }

    public LinkedList<GamePhase> getPastPhases() {
        return new LinkedList<>(pastPhases);
    }
}
