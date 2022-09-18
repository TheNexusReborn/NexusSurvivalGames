package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class DeathInfoEntity extends DeathInfo {
    protected final EntityType killer;

    public DeathInfoEntity(UUID player, EntityType killer) {
        super(player, DeathType.ENTITY);
        this.killer = killer;
    }

    public String getDeathMessage(Game game) {
        this.deathMessage = "&4&l>> %playername% &7was killed by a " + killer.name().toLowerCase().replace("_", "");
        return super.getDeathMessage(game);
    }
    
    public EntityType getKiller() {
        return killer;
    }
}
