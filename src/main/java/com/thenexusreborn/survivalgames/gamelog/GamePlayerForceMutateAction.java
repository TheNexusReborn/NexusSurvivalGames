package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.survivalgames.mutations.MutationType;

public class GamePlayerForceMutateAction extends GameAction {
    public GamePlayerForceMutateAction(String sender, String mutatedPlayer, MutationType type, String target, boolean bypassTimer) {
        super(System.currentTimeMillis(), "playerforcemutate", "");
        addValueData("actor", sender).addValueData("player", mutatedPlayer);
        addValueData("type", type.getDisplayName().toLowerCase().replace(" ", "_"));
        addValueData("target", target);
        addValueData("bypassTimer", bypassTimer);
    }
}
