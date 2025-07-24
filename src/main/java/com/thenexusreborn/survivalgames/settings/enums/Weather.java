package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum Weather {
    RAIN, STORM, CLEAR;
    
    static {
        StringConverters.addConverter(Weather.class, new EnumStringConverter<>(Weather.class));
    }
}
