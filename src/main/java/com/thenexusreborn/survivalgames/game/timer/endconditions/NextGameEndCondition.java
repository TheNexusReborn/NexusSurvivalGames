package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class NextGameEndCondition extends SGCallbackEndCondition {
    public NextGameEndCondition(Game game) {
        super(game, Game::nextGame, null, "next game", "start the next game", GameState.ENDING);
    }
}
