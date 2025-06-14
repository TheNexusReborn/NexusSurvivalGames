package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum WeatherType {
    RAIN, STORM, CLEAR;
    
    static {
        StringConverters.addConverter(WeatherType.class, new EnumStringConverter<>(WeatherType.class));
    }
}
