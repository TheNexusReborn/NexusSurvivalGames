package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class PigZombieMutation extends Mutation {
    protected PigZombieMutation(UUID player, UUID target) {
        super(MutationType.PIG_ZOMBIE, player, target);
    }
}
