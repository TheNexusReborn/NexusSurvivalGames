package com.thenexusreborn.survivalgames.game;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.gamemodes.ClassicGameSettings;
import com.thenexusreborn.survivalgames.settings.gamemodes.MutationMayhemGameSettings;

import java.util.function.Supplier;

public enum SGMode {
    CLASSIC(ClassicGameSettings::new, 
            "Loot chests scattered around the map for gear.", 
            "Outlast the other tributes and be the last one standing!"
    ),
    MUTATION_MAYHEM(MutationMayhemGameSettings::new, 
            "Loot chests to gear up, but be careful about killing others", 
            "Dead tributes may come back up to three times to seek revenge", 
            "The last Tribute standing wins.", 
            "Either kill everyone enough times, or be the only Tribute when time runs out."
    )/*, 
    UNDEAD, INFECTED, SOLO*/;
    
    static {
        StringConverters.addConverter(SGMode.class, new EnumStringConverter<>(SGMode.class));
    }
    
    private final Supplier<GameSettings> settingsSupplier;
    private final String[] description;
    
    SGMode() {
        this(GameSettings::new);
    }
    
    SGMode(Supplier<GameSettings> settingsSupplier, String... description) {
        this.settingsSupplier = settingsSupplier;
        this.description = description;
    }
    
    public Supplier<GameSettings> getSettingsSupplier() {
        return settingsSupplier;
    }
    
    public String[] getDescription() {
        return description;
    }
}
