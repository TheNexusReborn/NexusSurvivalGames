package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class SkeletonMutation extends Mutation {
    protected SkeletonMutation(UUID player, UUID target) {
        super(MutationType.SKELETON, player, target);
    }
}
