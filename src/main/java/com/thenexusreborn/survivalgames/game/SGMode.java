package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.gamemodes.ClassicGameSettings;

import java.util.*;

public enum SGMode {
    CLASSIC(new ClassicGameSettings(),
            Map.of(
                    GameModifier.MUTATIONS, GameModifierStatus.ALLOWED,
                    GameModifier.UNLIMITED_PASSES, GameModifierStatus.ALLOWED,
                    GameModifier.ALL_MUTATIONS, GameModifierStatus.ALLOWED,
                    GameModifier.ASSISTS, GameModifierStatus.ALLOWED,
                    GameModifier.BOUNTIES, GameModifierStatus.ALLOWED,
                    GameModifier.SPONSORS, GameModifierStatus.ALLOWED,
                    GameModifier.DEATHMATCH, GameModifierStatus.ALLOWED
            )),
    UNDEAD, INFECTED, SOLO;
    
    static {
        StringConverters.addConverter(SGMode.class, new EnumStringConverter<>(SGMode.class));
    }
    
    private final Map<GameModifier, GameModifierStatus> modifiers = new EnumMap<>(GameModifier.class);
    private final GameSettings defaultSettings;
    
    SGMode() {
        this(new GameSettings(), Map.of());
    }
    
    SGMode(GameSettings gameSettings, Map<GameModifier, GameModifierStatus> modifiers) {
        this.defaultSettings = gameSettings;
        this.modifiers.putAll(modifiers);
    }
    
    public Map<GameModifier, GameModifierStatus> getModifiers() {
        return new EnumMap<>(modifiers);
    }
    
    public GameSettings getDefaultSettings() {
        return defaultSettings;
    }
}
