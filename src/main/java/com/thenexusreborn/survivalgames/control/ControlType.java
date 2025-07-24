package com.thenexusreborn.survivalgames.control;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum ControlType {
    MANUAL("mnl", "m"), //No timers or progression logic is used. Progress triggered manually
    STEP, //Allows use of /sg game next to step forward in phases and steps
    SEMI_AUTO, //Timers run, but does not trigger next steps
    AUTO("automatic", "a"); //Fully auto, timers and progression logic
    
    static {
        StringConverters.addConverter(ControlType.class, new EnumStringConverter<>(ControlType.class));
    }
    
    private final String[] aliases;

    ControlType(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }
    
    public static ControlType match(String input) {
        try {
            return ControlType.valueOf(input.toUpperCase());
        } catch (Exception e) {
            for (ControlType value : values()) {
                if (value.getAliases() != null) {
                    for (String alias : value.getAliases()) {
                        if (alias.equalsIgnoreCase(input)) {
                            return value;
                        }
                    }
                }
            }
        }
        
        return null;
    }
}
