package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class DMWarmupEndCondition extends SGCallbackEndCondition {
    public DMWarmupEndCondition(Game game) {
        super(game, Game::startDeathmatch, Game::deathmatchWarmupDone, "deathmatch warmup", "start the deathmatch", GameState.DEATHMATCH_WARMUP, GameState.DEATHMATCH_WARMUP_DONE);
    }
}
