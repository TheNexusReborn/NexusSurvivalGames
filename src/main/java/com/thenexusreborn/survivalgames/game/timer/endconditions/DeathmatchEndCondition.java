package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class DeathmatchEndCondition extends SGCallbackEndCondition {
    public DeathmatchEndCondition(Game game) {
        super(game, Game::end, null, "game", "end the game", GameState.DEATHMATCH);
    }
}
