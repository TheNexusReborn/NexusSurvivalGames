package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;

public class RestockEndCondition extends SGCallbackEndCondition {
    public RestockEndCondition(Game game) {
        super(game, Game::restockChests, null, "restock", "restock chests", GameState.INGAME, GameState.INGAME_DEATHMATCH, GameState.DEATHMATCH);
    }
}
