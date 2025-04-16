package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class GraceperiodEndCondition extends SGCallbackEndCondition {
    public GraceperiodEndCondition(Game game) {
        super(game, Game::endGracePeriod, Game::markGraceperiodDone, "graceperiod", "end the graceperiod", Game.State.INGAME);
    }
}
