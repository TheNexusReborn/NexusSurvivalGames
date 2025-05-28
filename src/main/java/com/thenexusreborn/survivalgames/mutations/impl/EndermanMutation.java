package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class EndermanMutation extends Mutation {
    protected EndermanMutation(Game game, UUID player, UUID target) {
        super(game, StandardMutations.ENDERMAN, player, target);
    }
}