package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum Time {
    DAWN(23000), DAY(1000), NOON(6000), AFTERNOON(9000), DUSK(12000), NIGHT(13000), MIDNIGHT(22812);
    
    static {
        StringConverters.addConverter(Time.class, new EnumStringConverter<>(Time.class));
    }
    
    private final long start;
    
    Time(long start) {
        this.start = start;
    }
    
    public long getStart() {
        return start;
    }
}
