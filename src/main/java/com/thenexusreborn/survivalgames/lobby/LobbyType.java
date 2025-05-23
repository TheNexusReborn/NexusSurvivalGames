package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum LobbyType {
    CUSTOM, CLASSIC;
    
    static {
        StringConverters.addConverter(LobbyType.class, new EnumStringConverter<>(LobbyType.class));
    }
}