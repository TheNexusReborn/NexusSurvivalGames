package com.thenexusreborn.survivalgames.game;

public enum GameState {
    UNDEFINED, ERROR, SHUTTING_DOWN, 
    SETTING_UP, SETUP_COMPLETE,
    ASSIGN_TEAMS, TEAMS_ASSIGNED,
    TELEPORT_START, TELEPORT_START_DONE,
    WARMUP, WARMUP_DONE,
    INGAME, INGAME_DEATHMATCH,
    TELEPORT_DEATHMATCH, TELEPORT_DEATHMATCH_DONE, 
    DEATHMATCH_WARMUP, DEATHMATCH_WARMUP_DONE, 
    DEATHMATCH,
    GAME_COMPLETE, NEXT_GAME_READY,
    ENDING, ENDED
}
