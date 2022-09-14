package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierFourLootTable extends LootTable {
    public TierFourLootTable() {
        super("tierFour");
    
        LootCategory food = new LootCategory("food", Rarity.COMMON, 4);
        LootCategory armor = new LootCategory("armor", Rarity.UNCOMMON);
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
    
        food.addEntries(Rarity.COMMON, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.PUMPKIN_PIE);
        food.addEntry(Items.GOLDEN_CARROT, Rarity.UNCOMMON);
        food.addEntry(Items.GOLDEN_MUNCHIE, Rarity.RARE);
    
        armor.addEntries(Rarity.COMMON, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
        armor.addEntries(Rarity.RARE, Items.DIAMOND_BOOTS, Items.DIAMOND_HELMET);
        armor.addEntries(Rarity.EPIC, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS);
    
        weapons.addEntry(Items.STONE_SWORD, Rarity.COMMON);
        weapons.addEntry(Items.IRON_SWORD, Rarity.RARE);
    
        consumables.addEntries(Rarity.COMMON, Items.EGG_OF_DOOM, Items.SLOWBALL);
        consumables.addEntries(Rarity.UNCOMMON, Items.COBWEB, Items.XP_BOTTLE);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, Rarity.RARE));
        consumables.addEntries(Rarity.RARE, Items.TNT, Items.WET_NODDLE, Items.ENDER_PEARL);
    
        utilities.addEntries(Rarity.COMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(Rarity.COMMON, Items.GOLD_INGOT, Items.STICK);
        components.addEntry(Items.IRON_INGOT, Rarity.UNCOMMON);
        components.addEntry(Items.DIAMOND, Rarity.RARE);
    }
}
