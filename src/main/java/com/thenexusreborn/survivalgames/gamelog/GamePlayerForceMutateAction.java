package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.survivalgames.mutations.IMutationType;

public class GamePlayerForceMutateAction extends GameAction {
    public GamePlayerForceMutateAction(String sender, String mutatedPlayer, IMutationType type, String target, boolean bypassTimer) {
        super(System.currentTimeMillis(), "playerforcemutate");
        addValueData("actor", sender).addValueData("player", mutatedPlayer);
        addValueData("type", type.getDisplayName().toLowerCase().replace(" ", "_"));
        addValueData("target", target);
        addValueData("bypassTimer", bypassTimer);
    }
}
