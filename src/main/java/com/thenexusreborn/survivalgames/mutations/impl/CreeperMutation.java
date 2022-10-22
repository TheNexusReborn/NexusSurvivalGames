package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class CreeperMutation extends Mutation {
    protected CreeperMutation(UUID player, UUID target) {
        super(MutationType.CHICKEN, player, target);
    }
}
