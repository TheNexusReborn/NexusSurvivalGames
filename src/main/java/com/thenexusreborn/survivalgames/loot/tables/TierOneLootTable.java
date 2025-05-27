package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.item.Items;

import java.io.File;

public class TierOneLootTable extends SGLootTable {
    public TierOneLootTable(File folder) {
        super("tierOne", new File(folder, "tierOne.yml"));
    }

    @Override
    public void loadDefaultData() {
        addItems(30, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.VILE_CREATURE, Items.BAKED_POTATO, Items.PUMPKIN_PIE);
        addItems(50, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.RAW_FISH);
        addItems(35, Items.CAKE, Items.MELON, Items.CARROT, Items.APPLE, Items.MUSHROOM_SOUP);
        addItems(15, Items.GOLDEN_CARROT, Items.WET_NODDLE);

        addItems(30, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
        addItems(20, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        addItems(5, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.FLINT_AND_STEEL);
        
        addItems(30, Items.WOOD_SWORD, Items.STONE_AXE, Items.BOW);
        addItems(15, Items.STONE_SWORD);

        addItems(20, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE);
        addItems(10, Items.COBWEB, Items.TNT);

        addItems(30, Items.ARROW);
        addItems(15, Items.PLAYER_TRACKER, Items.FISHING_ROD);

        addItems(20, Items.FEATHER, Items.FLINT, Items.STICK);
        addItems(20, Items.GOLD_INGOT, Items.IRON_INGOT);
        addItems(3, Items.ENDER_PEARL, Items.DIAMOND, Items.GOLDEN_MUNCHIE);
    }
}
