package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class WarmupEndCondition extends SGCallbackEndCondition {
    
    public WarmupEndCondition(Game game) {
        super(game, Game::startGame, Game::warmupComplete, "warmup", "start the game", Game.State.WARMUP, Game.State.WARMUP_DONE);
    }
}
