package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum LootMode {
    CLASSIC, TIERED, RANDOM;
    
    static {
        StringConverters.addConverter(LootMode.class, new EnumStringConverter<>(LootMode.class));
    }
}