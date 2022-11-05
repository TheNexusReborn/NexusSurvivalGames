package com.thenexusreborn.survivalgames.game.death;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DeathType {
    UNKNOWN, PLAYER, PLAYER_PROJECTILE, ENTITY, ENTITY_PROJECTILE, SUFFOCATION, MELTING, FALL, LAVA, FIRE, DROWNING, EXPLOSION, VOID, LIGHTNING, SUICIDE, STARVATION, POISON, MAGIC, WITHER, INCOGNITO, FALLING_BLOCK, THORNS, CHEAT, LEAVE, VANISH, SPECTATE;
    
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
}
