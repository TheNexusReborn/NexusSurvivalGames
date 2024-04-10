package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;

public class DMWarmupEndCondition extends SGCallbackEndCondition {
    public DMWarmupEndCondition(Game game) {
        super(game, Game::startDeathmatch, Game::deathmatchWarmupDone, "deathmatch warmup", "start the deathmatch", OldGameState.DEATHMATCH_WARMUP, OldGameState.DEATHMATCH_WARMUP_DONE);
    }
}
