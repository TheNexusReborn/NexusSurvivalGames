package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;

public class PlayerManageBuilder {
    protected final SGPlayer actor;
    protected final GamePlayer target;
    protected SGLootTable lootTable;
    protected int numberOfItems;

    public PlayerManageBuilder(SGPlayer actor, GamePlayer target) {
        this.actor = actor;
        this.target = target;
    }

    public PlayerManageBuilder lootTable(SGLootTable lootTable) {
        this.lootTable = lootTable;
        return this;
    }

    public PlayerManageBuilder numberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
        return this;
    }

    public GamePlayer getTarget() {
        return target;
    }

    public SGPlayer getActor() {
        return actor;
    }

    public SGLootTable getLootTable() {
        return lootTable;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }
}
