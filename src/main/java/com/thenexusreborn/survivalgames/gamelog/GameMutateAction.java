package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.survivalgames.mutations.MutationType;

public class GameMutateAction extends GameAction {
    public GameMutateAction(String player, String target, MutationType type) {
        super(System.currentTimeMillis(), "mutate");
        addValueData("mutator", player).addValueData("target", target).addValueData("type", type.getDisplayName().toLowerCase().replace(" ", "_"));
    }
}
