package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.gamemodes.ClassicGameSettings;

import java.util.*;

public enum SGMode {
    CLASSIC(new ClassicGameSettings()),
    UNDEAD, INFECTED, SOLO;
    
    static {
        StringConverters.addConverter(SGMode.class, new EnumStringConverter<>(SGMode.class));
    }
    
    private final GameSettings defaultSettings;
    
    SGMode() {
        this(new GameSettings());
    }
    
    SGMode(GameSettings gameSettings) {
        this.defaultSettings = gameSettings;
    }
    
    public GameSettings getDefaultSettings() {
        return defaultSettings;
    }
}
