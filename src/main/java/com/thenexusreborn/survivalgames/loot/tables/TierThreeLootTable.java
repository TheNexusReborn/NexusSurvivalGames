package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierThreeLootTable extends LootTable {
    public TierThreeLootTable() {
        super("tierThree");
    
        LootCategory food = new LootCategory("food", Rarity.COMMON, 4);
        LootCategory armor = new LootCategory("armor", Rarity.COMMON);
        LootCategory weapons = new LootCategory("weapons", Rarity.UNCOMMON);
        LootCategory consumables = new LootCategory("consumables", Rarity.UNCOMMON);
        LootCategory utilities = new LootCategory("utilities", Rarity.RARE);
        LootCategory components = new LootCategory("components", Rarity.UNCOMMON);
    
        addCategory(food);
        addCategory(armor);
        addCategory(weapons);
        addCategory(consumables);
        addCategory(utilities);
        addCategory(components);
    
        food.addEntries(Rarity.COMMON, Items.BAKED_POTATO, Items.APPLE, Items.VILE_CREATURE);
        food.addEntries(Rarity.UNCOMMON, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE);
        food.addEntry(Rarity.RARE, Items.GOLDEN_CARROT);
        food.addEntry(Rarity.EPIC, Items.GOLDEN_MUNCHIE);
    
        armor.addEntries(Rarity.COMMON, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(Rarity.RARE, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntries(Rarity.COMMON, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(Rarity.UNCOMMON, Items.STONE_SWORD);
        weapons.addEntry(Rarity.RARE, Items.IRON_SWORD);
    
        consumables.addEntries(Rarity.COMMON, Items.EGG_OF_DOOM, Items.SLOWBALL);
        consumables.addEntries(Rarity.UNCOMMON, Items.COBWEB, Items.XP_BOTTLE);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, Rarity.RARE.getMax()));
        consumables.addEntries(Rarity.RARE, Items.TNT, Items.WET_NODDLE);
        consumables.addEntry(Rarity.EPIC, Items.ENDER_PEARL);
    
        utilities.addEntries(Rarity.COMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(Rarity.COMMON, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT, Items.IRON_INGOT);
        components.addEntry(Rarity.UNCOMMON, Items.DIAMOND);
    }
}
