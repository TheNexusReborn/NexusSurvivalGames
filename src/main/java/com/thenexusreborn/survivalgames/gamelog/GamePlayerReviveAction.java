package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GamePlayerReviveAction extends GameAction {
    public GamePlayerReviveAction(String sender, String revivedPlayer, String lootTable, int amount) {
        super(System.currentTimeMillis(), "playerrevive", "");
        addValueData("actor", sender).addValueData("player", revivedPlayer);
        if (lootTable != null && !lootTable.isEmpty()) {
            addValueData("loottable", lootTable);
            addValueData("itemcount", amount);
        }
    }
    
    public GamePlayerReviveAction(String sender, String revivedPlayer) {
        this(sender, revivedPlayer, null, 0);
    }
}
