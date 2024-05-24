package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

public class TierFourLootTable extends SGLootTable {
    public TierFourLootTable() {
        super("tierFour");
        addItems(40, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        addItems(30, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);

        addItems(40, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(20, Items.STONE_SWORD, Items.IRON_SWORD, Items.BOW);

        addItems(20, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        addItems(15, Items.COBWEB, Items.TNT);

        addItems(20, Items.ARROW.setAmount(5));
        addItems(15, Items.FLINT_AND_STEEL, Items.FISHING_ROD);

        addItems(15, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
