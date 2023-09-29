package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class WarmupEndCondition extends SGCallbackEndCondition {
    public WarmupEndCondition(Game game) {
        super(game, Game::startGame, Game::warmupComplete, "warmup", "start the game", GameState.WARMUP, GameState.WARMUP_DONE);
    }
}
