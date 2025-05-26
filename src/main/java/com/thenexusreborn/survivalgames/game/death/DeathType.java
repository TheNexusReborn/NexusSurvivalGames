package com.thenexusreborn.survivalgames.game.death;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.io.*;

public enum DeathType {
    UNKNOWN, 
    PLAYER,
    PLAYER_PROJECTILE,
    ENTITY,
    ENTITY_PROJECTILE,
    SUFFOCATION(true),
    MELTING(true),
    FALL(true),
    LAVA(true),
    FIRE(true),
    DROWNING(true),
    EXPLOSION(true),
    VOID(true),
    LIGHTNING(true),
    SUICIDE,
    STARVATION(true),
    POISON(true),
    MAGIC(true),
    WITHER(true), 
    INCOGNITO(true), 
    FALLING_BLOCK(true), 
    THORNS(true),
    CHEAT, 
    LEAVE(true), 
    VANISH(true),
    SPECTATE, 
    CACTUS(true), LEAVE_ARENA;
    
    static {
        StringConverters.addConverter(DeathType.class, new EnumStringConverter<>(DeathType.class));
    }
    
    private final boolean playerSubtype;
    
    DeathType() {
        this.playerSubtype = false;
    }
    
    DeathType(boolean playerSubtype) {
        this.playerSubtype = playerSubtype;
    }
    
    public static DeathType getTypeByCause(DamageCause cause) {
        for (DeathType deathType : values()) {
            if (deathType.name().equalsIgnoreCase(cause.name())) {
                return deathType;
            }
            
            if (cause.name().contains("EXPLOSION")) {
                return EXPLOSION;
            }
        }
        
        return UNKNOWN;
    }
    
    public static void generateDefaultDeathMessages(File file, FileConfiguration config) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        for (DeathType deathType : DeathType.values()) {
            if (deathType.hasPlayerSubtype()) {
                config.set(deathType.name() + ".normal", "%playername%");
                config.set(deathType.name() + ".player", "%playername% %killername%");
            } else {
                config.set(deathType.name(), "%playername%");
            }
        }
    
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasPlayerSubtype() {
        return playerSubtype;
    }
}
