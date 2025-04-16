package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class DMWarmupEndCondition extends SGCallbackEndCondition {
    public DMWarmupEndCondition(Game game) {
        super(game, Game::startDeathmatch, Game::deathmatchWarmupDone, "deathmatch warmup", "start the deathmatch", Game.State.DEATHMATCH_WARMUP, Game.State.DEATHMATCH_WARMUP_DONE);
    }
}
