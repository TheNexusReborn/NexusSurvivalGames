package com.thenexusreborn.survivalgames.settings.enums;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum WorldTime {
    DAWN(23000), DAY(1000), NOON(6000), AFTERNOON(9000), DUSK(12000), NIGHT(13000), MIDNIGHT(22812);
    
    static {
        StringConverters.addConverter(WorldTime.class, new EnumStringConverter<>(WorldTime.class));
    }
    
    private final long start;
    
    WorldTime(long start) {
        this.start = start;
    }
    
    public long getStart() {
        return start;
    }
}
