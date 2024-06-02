package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GameGiveAction extends GameAction {
    public GameGiveAction(String sender, String target, String item) {
        super(System.currentTimeMillis(), target.equals("all") ? "giveall" : "giveitem", "");
        addValueData("sender", sender).addValueData("item", item);
    }
}
