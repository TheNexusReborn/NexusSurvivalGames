package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GamePlayerAddAction extends GameAction {
    public GamePlayerAddAction(String sender, String addedPlayer, String lootTable, int amount) {
        super(System.currentTimeMillis(), "playeradd");
        addValueData("actor", sender).addValueData("player", addedPlayer);
        if (lootTable != null && !lootTable.isEmpty()) {
            addValueData("loottable", lootTable);
            addValueData("itemcount", amount);
        }
    }
    
    public GamePlayerAddAction(String sender, String addedPlayer) {
        this(sender, addedPlayer, null, 0);
    }
}
