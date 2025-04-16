package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class InGameEndCondition extends SGCallbackEndCondition {
    public InGameEndCondition(Game game) {
        super(game, g -> {
            if (g.getSettings().isAllowDeathmatch()) {
                g.teleportDeathmatch();
            } else {
                g.end();
            }
        }, null, "game", "start the deathmatch warmup", Game.State.INGAME);
    }
}
