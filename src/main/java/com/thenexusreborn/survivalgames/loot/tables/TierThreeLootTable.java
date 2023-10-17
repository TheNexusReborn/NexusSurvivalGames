package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierThreeLootTable extends LootTable {
    public TierThreeLootTable() {
        super("tierThree");
    
        LootCategory food = new LootCategory("food", 45, 4);
        LootCategory armor = new LootCategory("armor", 45);
        LootCategory weapons = new LootCategory("weapons", 17);
        LootCategory consumables = new LootCategory("consumables", 17);
        LootCategory utilities = new LootCategory("utilities", 8);
        LootCategory components = new LootCategory("components", 17);
    
        addCategory(food);
        addCategory(armor);
        addCategory(weapons);
        addCategory(consumables);
        addCategory(utilities);
        addCategory(components);
    
        food.addEntries(45, Items.BAKED_POTATO, Items.APPLE, Items.VILE_CREATURE);
        food.addEntries(17, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE);
        food.addEntry(8, Items.GOLDEN_CARROT);
        food.addEntry(3, Items.GOLDEN_MUNCHIE);
    
        armor.addEntries(45, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(8, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntries(45, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(17, Items.STONE_SWORD);
        weapons.addEntry(8, Items.IRON_SWORD);
    
        consumables.addEntries(45, Items.EGG_OF_DOOM, Items.SLOWBALL);
        consumables.addEntries(17, Items.COBWEB, Items.XP_BOTTLE);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, 11));
        consumables.addEntries(8, Items.TNT, Items.WET_NODDLE);
        consumables.addEntry(3, Items.ENDER_PEARL);
    
        utilities.addEntries(45, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(45, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT);
        components.addEntry(17, Items.DIAMOND);
    }
}
