package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class DeathmatchEndCondition extends SGCallbackEndCondition {
    public DeathmatchEndCondition(Game game) {
        super(game, Game::end, null, "game", "end the game", Game.State.DEATHMATCH);
    }
}
