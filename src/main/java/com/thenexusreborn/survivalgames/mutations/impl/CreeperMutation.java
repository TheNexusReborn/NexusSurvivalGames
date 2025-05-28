package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class CreeperMutation extends Mutation {
    protected CreeperMutation(Game game, UUID player, UUID target) {
        super(game, StandardMutations.CREEPER, player, target);
    }
}
