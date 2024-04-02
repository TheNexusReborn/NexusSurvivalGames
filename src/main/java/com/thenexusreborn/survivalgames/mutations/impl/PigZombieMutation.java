package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class PigZombieMutation extends Mutation {
    protected PigZombieMutation(Game game, UUID player, UUID target) {
        super(game, MutationType.PIG_ZOMBIE, player, target);
    }
}
