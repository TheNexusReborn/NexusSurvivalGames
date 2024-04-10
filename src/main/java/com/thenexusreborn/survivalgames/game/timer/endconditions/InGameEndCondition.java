package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;

public class InGameEndCondition extends SGCallbackEndCondition {
    public InGameEndCondition(Game game) {
        super(game, Game::startDeathmatchTimer, null, "game", "start the deathmatch countdown", OldGameState.INGAME);
    }
}
