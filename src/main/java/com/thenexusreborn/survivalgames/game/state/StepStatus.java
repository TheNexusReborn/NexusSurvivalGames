package com.thenexusreborn.survivalgames.game.state;

/**
 * This is a common interface to use for the status enums
 */
@FunctionalInterface
public interface StepStatus {
    enum Defaults implements StepStatus {
        STARTING, COMPLETE, ERROR
    }
    
    StepStatus STARTING = Defaults.STARTING;
    StepStatus COMPLETE = Defaults.COMPLETE;
    StepStatus ERROR = Defaults.ERROR;
    
    String name();
    String toString();
    boolean equals(Object o);
}
