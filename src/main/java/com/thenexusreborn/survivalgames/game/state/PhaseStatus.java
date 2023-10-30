package com.thenexusreborn.survivalgames.game.state;

/**
 * This is a common interface to use for the status enums
 */
@FunctionalInterface
public interface PhaseStatus {
    enum Defaults implements PhaseStatus {
        STARTING, COMPLETE
    }
    
    PhaseStatus STARTING = Defaults.STARTING;
    PhaseStatus COMPLETE = Defaults.COMPLETE;
    
    String name();
    String toString();
}
