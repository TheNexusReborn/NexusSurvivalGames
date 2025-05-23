package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum Graceperiod {
    ACTIVE, TIMER_DONE, INACTIVE;
    
    static {
        StringConverters.addConverter(Graceperiod.class, new EnumStringConverter<>(Graceperiod.class));
    }
}
