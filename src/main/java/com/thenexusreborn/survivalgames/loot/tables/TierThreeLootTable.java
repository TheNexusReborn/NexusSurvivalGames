package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

import java.io.File;

public class TierThreeLootTable extends SGLootTable {
    public TierThreeLootTable(File folder) {
        super("tierThree", new File(folder, "tierThree.yml"));
    }

    @Override
    public void loadDefaultData() {
        addItems(50, Items.BAKED_POTATO, Items.VILE_CREATURE, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        addItems(40, Items.APPLE, Items.CAKE, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);

        addItems(30, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        addItems(20, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(30, Items.STONE_AXE, Items.BOW);
        addItems(20, Items.STONE_SWORD);
        addItems(10, Items.IRON_SWORD);

        addItems(15, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        addItems(10, Items.COBWEB, Items.TNT);

        addItems(30, Items.ARROW.setAmount(5));
        addItems(25, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);

        addItems(30, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT);
        addItems(20, Items.IRON_INGOT);
        addItems(5, Items.DIAMOND);
    }
}
