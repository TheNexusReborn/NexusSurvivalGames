package com.thenexusreborn.survivalgames;

public enum ControlType {
    MANUAL, //No timers or progression logic is used. Progress triggered manually
    STEP, //Allows use of /sg game next to step forward in phases and steps
    SEMI_AUTO, //Timers run, but does not trigger next steps
    AUTO //Fully auto, timers and progression logic
}
