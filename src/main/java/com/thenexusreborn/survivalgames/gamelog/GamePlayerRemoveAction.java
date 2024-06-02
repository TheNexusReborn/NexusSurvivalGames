package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GamePlayerRemoveAction extends GameAction {
    public GamePlayerRemoveAction(String sender, String removedPlayer) {
        super(System.currentTimeMillis(), "playerremove");
        addValueData("actor", sender).addValueData("player", removedPlayer);
    }
}
