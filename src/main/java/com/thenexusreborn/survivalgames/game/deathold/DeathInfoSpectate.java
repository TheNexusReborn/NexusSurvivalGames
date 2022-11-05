package com.thenexusreborn.survivalgames.game.deathold;

import java.util.UUID;

public class DeathInfoSpectate extends DeathInfo {
    public DeathInfoSpectate(UUID player, String teamColor) {
        super(player, DeathType.SPECTATE, "&4&l>> %playername% &7decided to spectate", teamColor);
    }
}
