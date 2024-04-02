package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class SkeletonMutation extends Mutation {
    protected SkeletonMutation(Game game, UUID player, UUID target) {
        super(game, MutationType.SKELETON, player, target);
    }
}
