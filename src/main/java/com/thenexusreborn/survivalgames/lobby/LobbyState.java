package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.state.IState;

public enum LobbyState implements IState {
    SHUTTING_DOWN, WAITING, COUNTDOWN, STARTING, PREPARING_GAME, GAME_PREPARED, MAP_CONFIGURATING, SETUP;
    
    static {
        StringConverters.addConverter(LobbyState.class, new EnumStringConverter<>(LobbyState.class));
    }
}
