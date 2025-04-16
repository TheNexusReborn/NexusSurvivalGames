package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class NextGameEndCondition extends SGCallbackEndCondition {
    public NextGameEndCondition(Game game) {
        super(game, Game::nextGame, null, "next game", "start the next game", Game.State.ENDING);
    }
}
