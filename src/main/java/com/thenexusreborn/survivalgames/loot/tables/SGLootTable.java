package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.SGPlayer;

public abstract class SGLootTable extends LootTable {
    
    protected SGPlayer player;
    
    public SGLootTable(SGPlayer sgPlayer, String name) {
        super(name);
        this.player = sgPlayer;
    }
    
    public SGLootTable(String name) {
        super(name);
    }

    public SGPlayer getPlayer() {
        return player;
    }
}
