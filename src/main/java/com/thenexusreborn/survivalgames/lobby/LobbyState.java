package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.survivalgames.state.IState;

public enum LobbyState implements IState {
    SHUTTING_DOWN, WAITING, COUNTDOWN, STARTING, PREPARING_GAME, GAME_PREPARED, MAP_CONFIGURATING, SETUP
}
