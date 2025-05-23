package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum Weather {
    RAIN, STORM, CLEAR;
    
    static {
        StringConverters.addConverter(Weather.class, new EnumStringConverter<>(Weather.class));
    }
}
