package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum ColorMode {
    RANK, GAME_TEAM;
    
    static {
        StringConverters.addConverter(ColorMode.class, new EnumStringConverter<>(ColorMode.class));
    }
}
