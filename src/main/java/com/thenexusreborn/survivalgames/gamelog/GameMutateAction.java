package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.survivalgames.mutations.IMutationType;

public class GameMutateAction extends GameAction {
    public GameMutateAction(String player, String target, IMutationType type) {
        super(System.currentTimeMillis(), "mutate");
        addValueData("mutator", player).addValueData("target", target).addValueData("type", type.getDisplayName().toLowerCase().replace(" ", "_"));
    }
}
