package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.game.Game;

public class RestockEndCondition extends SGCallbackEndCondition {
    public RestockEndCondition(Game game) {
        super(game, Game::restockChests, null, "restock", "restock chests", Game.State.INGAME, Game.State.INGAME_DEATHMATCH, Game.State.DEATHMATCH);
    }
}
