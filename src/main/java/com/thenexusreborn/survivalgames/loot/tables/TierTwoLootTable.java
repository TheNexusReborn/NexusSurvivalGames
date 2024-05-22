package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

public class TierTwoLootTable extends SGLootTable {
    public TierTwoLootTable() {
        super("tierTwo");
        addItems(1, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE, Items.VILE_CREATURE, Items.BAKED_POTATO);
        addItems(1, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.RAW_FISH);
        addItems(1, Items.APPLE, Items.CAKE, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);

        addItems(1, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS);
        addItems(1, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        addItems(1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(1, Items.WOOD_SWORD);
        addItems(1, Items.STONE_AXE, Items.BOW);
        addItems(1, Items.STONE_SWORD);

        addItems(1, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        addItems(1, Items.COBWEB, Items.TNT);

        addItems(1, Items.ARROW);
        addItems(1, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);

        addItems(1, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
