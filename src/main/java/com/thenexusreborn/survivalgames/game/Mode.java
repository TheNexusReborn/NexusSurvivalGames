package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum Mode {
    CLASSIC, UNDEAD, INFECTED;
    
    static {
        StringConverters.addConverter(Mode.class, new EnumStringConverter<>(Mode.class));
    }
}
