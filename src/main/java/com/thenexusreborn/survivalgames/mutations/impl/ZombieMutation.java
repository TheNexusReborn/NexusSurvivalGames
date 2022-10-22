package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class ZombieMutation extends Mutation {
    protected ZombieMutation(UUID player, UUID target) {
        super(MutationType.ZOMBIE, player, target);
    }
}
