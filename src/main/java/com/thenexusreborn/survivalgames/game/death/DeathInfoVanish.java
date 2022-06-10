package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.UUID;

public class DeathInfoVanish extends DeathInfo {
    public DeathInfoVanish(UUID player) {
        super(player, DeathType.VANISH);
    }
    
    @Override
    public String getDeathMessage(Game game) {
        this.deathMessage = "&4&l>> %playername% &7suicided";
        return super.getDeathMessage(game);
    }
}
