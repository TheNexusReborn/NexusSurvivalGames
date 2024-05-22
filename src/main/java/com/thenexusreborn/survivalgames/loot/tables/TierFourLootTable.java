package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

public class TierFourLootTable extends SGLootTable {
    public TierFourLootTable() {
        super("tierFour");
        addItems(1, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        addItems(1, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);

        addItems(1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(1, Items.STONE_SWORD, Items.IRON_SWORD);

        addItems(1, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        addItems(1, Items.COBWEB, Items.TNT);

        addItems(1, Items.ARROW.setAmount(5));
        addItems(1, Items.FLINT_AND_STEEL, Items.FISHING_ROD);

        addItems(1, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
