package com.thenexusreborn.survivalgames.game.state;

/**
 * This is a common interface to use for the status enums
 */
@FunctionalInterface
public interface PhaseStatus {
    String name();
    String toString();
}
