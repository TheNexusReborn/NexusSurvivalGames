package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class GraceperiodEndCondition extends SGCallbackEndCondition {
    public GraceperiodEndCondition(Game game) {
        super(game, Game::endGracePeriod, Game::markGraceperiodDone, "graceperiod", "end the graceperiod", GameState.INGAME);
    }
}
