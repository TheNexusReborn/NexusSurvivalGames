package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class DMTimerEndCondition extends SGCallbackEndCondition {
    public DMTimerEndCondition(Game game) {
        super(game, Game::teleportDeathmatch, null, "deathmatch countdown", "start the deathmatch warmup", GameState.INGAME_DEATHMATCH);
    }
}
