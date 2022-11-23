package com.thenexusreborn.survivalgames.game.death;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.io.*;

public enum DeathType {
    UNKNOWN, 
    PLAYER, //
    PLAYER_PROJECTILE, //
    ENTITY, //
    ENTITY_PROJECTILE, //
    SUFFOCATION(true), //done
    MELTING(true), //done
    FALL(true), //done
    LAVA(true), //done
    FIRE(true), //done
    DROWNING(true), //done
    EXPLOSION(true), //done
    VOID(true), //done
    LIGHTNING(true), //done
    SUICIDE, //done
    STARVATION(true), //done
    POISON(true), //done
    MAGIC(true),//done
    WITHER(true), //done
    INCOGNITO(true), //done
    FALLING_BLOCK(true), //done
    THORNS(true), //done
    CHEAT, //done
    LEAVE(true), //done
    VANISH(true), //done
    SPECTATE, //done
    CACTUS(true); //done
    
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
