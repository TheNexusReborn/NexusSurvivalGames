package com.thenexusreborn.survivalgames.game.death;

import java.util.UUID;

public class DeathInfoVanish extends DeathInfo {
    public DeathInfoVanish(UUID player) {
        super(player, DeathType.VANISH, "%playername& suicided.");
    }
}
