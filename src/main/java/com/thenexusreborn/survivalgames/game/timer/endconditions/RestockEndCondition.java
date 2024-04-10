package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;

public class RestockEndCondition extends SGCallbackEndCondition {
    public RestockEndCondition(Game game) {
        super(game, Game::restockChests, null, "restock", "restock chests", OldGameState.INGAME, OldGameState.INGAME_DEATHMATCH, OldGameState.DEATHMATCH);
    }
}
