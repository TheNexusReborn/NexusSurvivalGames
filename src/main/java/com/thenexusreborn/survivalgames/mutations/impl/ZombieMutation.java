package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class ZombieMutation extends Mutation {
    protected ZombieMutation(Game game, UUID player, UUID target) {
        super(game, MutationType.ZOMBIE, player, target);
    }
}
