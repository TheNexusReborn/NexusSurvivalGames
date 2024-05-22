package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

public class TierThreeLootTable extends SGLootTable {
    public TierThreeLootTable() {
        super("tierThree");
        addItems(1, Items.BAKED_POTATO, Items.VILE_CREATURE, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        addItems(1, Items.APPLE, Items.CAKE, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);

        addItems(1, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        addItems(1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(1, Items.STONE_AXE, Items.BOW);
        addItems(1, Items.STONE_SWORD);
        addItems(1, Items.IRON_SWORD);

        addItems(1, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        addItems(1, Items.COBWEB, Items.TNT);

        addItems(1, Items.ARROW.setAmount(5));
        addItems(1, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);

        addItems(1, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
