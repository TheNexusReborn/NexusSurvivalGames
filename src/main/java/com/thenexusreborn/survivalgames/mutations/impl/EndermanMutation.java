package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.survivalgames.mutations.*;

import java.util.UUID;

public class EndermanMutation extends Mutation {
    protected EndermanMutation(UUID player, UUID target) {
        super(MutationType.ENDERMAN, player, target);
    }
}