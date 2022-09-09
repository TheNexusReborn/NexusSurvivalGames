package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierThreeLootTable extends LootTable {
    public TierThreeLootTable() {
        super("tierThree");
    
        LootCategory food = new LootCategory("food", Rarity.COMMON);
        LootCategory consumables = new LootCategory("consumables", Rarity.COMMON);
        LootCategory utilities = new LootCategory("utilities", Rarity.UNCOMMON);
        LootCategory components = new LootCategory("components", Rarity.UNCOMMON);
        LootCategory weapons = new LootCategory("weapons", Rarity.UNCOMMON);
        LootCategory armor = new LootCategory("armor", Rarity.RARE);
    
        addCategory(food);
        addCategory(armor);
        addCategory(weapons);
        addCategory(consumables);
        addCategory(utilities);
        addCategory(components);
    
        food.addEntries(Rarity.UNCOMMON, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE, Items.VILE_CREATURE);
        food.addEntries(Rarity.COMMON, Items.BAKED_POTATO, Items.APPLE);
        food.addEntries(Rarity.RARE, Items.GOLDEN_CARROT, Items.GOLDEN_MUNCHIE);
    
        armor.addEntries(Rarity.COMMON, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
        armor.addEntries(Rarity.EPIC, Items.DIAMOND_HELMET, Items.DIAMOND_BOOTS);
        armor.addEntries(Rarity.LEGENDARY, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_BOOTS);
    
        weapons.addEntries(Rarity.COMMON, Items.BOW);
        weapons.addEntry(Items.IRON_SWORD, Rarity.RARE);
    
        consumables.addEntries(Rarity.COMMON, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.COBWEB);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, Rarity.UNCOMMON));
        consumables.addEntries(Rarity.UNCOMMON, Items.TNT, Items.WET_NODDLE);
        consumables.addEntries(Rarity.RARE, Items.XP_BOTTLE, Items.ENDER_PEARL);
    
        utilities.addEntries(Rarity.UNCOMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntry(Items.IRON_INGOT, Rarity.UNCOMMON);
        components.addEntries(Rarity.COMMON, Items.FEATHER, Items.FLINT, Items.GOLD_INGOT, Items.STICK);
        components.addEntry(Items.DIAMOND, Rarity.RARE);
    }
}
