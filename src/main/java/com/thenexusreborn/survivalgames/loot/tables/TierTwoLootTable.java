package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

import java.io.File;

public class TierTwoLootTable extends SGLootTable {
    public TierTwoLootTable(File folder) {
        super("tierTwo", new File(folder, "tierTwo.yml"));
    }

    @Override
    public void loadDefaultData() {
        addItems(40, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE, Items.VILE_CREATURE, Items.BAKED_POTATO);
        addItems(50, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.RAW_FISH);
        addItems(45, Items.APPLE, Items.CAKE, Items.GOLDEN_CARROT);
        addItems(8, Items.GOLDEN_MUNCHIE);

        addItems(35, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS);
        addItems(25, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        addItems(5, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

        addItems(30, Items.WOOD_SWORD);
        addItems(20, Items.STONE_AXE, Items.BOW);
        addItems(15, Items.STONE_SWORD);

        addItems(30, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE);
        addItems(15, Items.COBWEB, Items.TNT);

        addItems(20, Items.ARROW.setAmount(5));
        addItems(15, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD, Items.ENDER_PEARL);

        addItems(25, Items.FEATHER, Items.FLINT, Items.STICK);
        addItems(20, Items.GOLD_INGOT, Items.IRON_INGOT);
        addItems(5, Items.DIAMOND);
    }
}
