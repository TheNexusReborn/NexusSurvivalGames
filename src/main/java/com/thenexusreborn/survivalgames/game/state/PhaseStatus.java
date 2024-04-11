package com.thenexusreborn.survivalgames.game.state;

/**
 * This is a common interface to use for the status enums
 */
@FunctionalInterface
public interface PhaseStatus {
    enum Defaults implements PhaseStatus {
        STARTING, COMPLETE, ERROR
    }
    
    PhaseStatus STARTING = Defaults.STARTING;
    PhaseStatus COMPLETE = Defaults.COMPLETE;
    PhaseStatus ERROR = Defaults.ERROR;
    
    String name();
    String toString();
    boolean equals(Object o);
}
