package com.thenexusreborn.survivalgames.disguises.disguisetypes;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum RabbitType {

    BLACK(2), BROWN(0), GOLD(4), KILLER_BUNNY(99), PATCHES(3), PEPPER(5), WHITE(1);
    
    static {
        StringConverters.addConverter(RabbitType.class, new EnumStringConverter<>(RabbitType.class));
    }

    public static RabbitType getType(int id) {
        for (RabbitType type : values()) {
            if (type.getTypeId() == id) {
                return type;
            }
        }
        return null;
    }

    private final int type;

    RabbitType(int type) {
        this.type = type;
    }

    public int getTypeId() {
        return type;
    }
}
