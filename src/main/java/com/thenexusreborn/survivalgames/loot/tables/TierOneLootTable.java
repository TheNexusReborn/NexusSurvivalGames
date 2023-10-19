package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierOneLootTable extends CommonLootTable {
    public TierOneLootTable() {
        super("tierOne");
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
    
        cookedFood.addEntries(1, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.VILE_CREATURE, Items.BAKED_POTATO, Items.PUMPKIN_PIE);
        rawFood.addEntries(1, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.POTATO, Items.RAW_FISH, Items.RAW_CHICKEN);
        miscFood.addEntries(1, Items.CAKE, Items.MELON, Items.CARROT, Items.APPLE, Items.COOKIE, Items.MUSHROOM_SOUP, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE, Items.WET_NODDLE);
    
        armor.addEntries(1, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
        armor.addEntries(1, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntry(1, Items.WOOD_AXE);
        weapons.addEntries(1, Items.WOOD_SWORD, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(1, Items.STONE_SWORD);
        
        throwables.addEntries(1, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.XP_BOTTLE, Items.ENDER_PEARL);
        placeables.addEntries(1, Items.COBWEB, Items.TNT);
    
        tools.addEntry(new LootEntry(Items.ARROW, 5, 1));
        tools.addEntries(1, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(1, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT, Items.DIAMOND);
    }
}
