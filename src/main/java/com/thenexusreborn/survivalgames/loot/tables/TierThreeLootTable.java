package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierThreeLootTable extends CommonLootTable {
    public TierThreeLootTable() {
        super("tierThree");
        cookedFood = new LootCategory("cookedfood", 1, 4);
        rawFood = new LootCategory("rawfood", 1, 4);
        miscFood = new LootCategory("miscfood", 1, 4);
        armor = new LootCategory("armor", 1);
        weapons = new LootCategory("weapons", 1);
        components = new LootCategory("components", 1);
        throwables = new LootCategory("throwables", 1);
        placeables = new LootCategory("placeables", 1);
        tools = new LootCategory("tools", 1);

        registerCategories();
        
        cookedFood.addEntries(1, Items.BAKED_POTATO, Items.VILE_CREATURE, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        miscFood.addEntries(1, Items.APPLE, Items.CAKE, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);
    
        armor.addEntries(1, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntries(1, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(1, Items.STONE_SWORD);
        weapons.addEntry(1, Items.IRON_SWORD);

        throwables.addEntries(1, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        placeables.addEntries(1, Items.COBWEB, Items.TNT);

        tools.addEntry(new LootEntry(Items.ARROW, 7, 1));
        tools.addEntries(1, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(1, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
