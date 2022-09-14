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
        food.addEntry(Items.GOLDEN_CARROT, Rarity.RARE);
        food.addEntry(Items.GOLDEN_MUNCHIE, Rarity.EPIC);
    
        armor.addEntries(Rarity.COMMON, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(Rarity.RARE, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
        armor.addEntries(Rarity.EPIC, Items.DIAMOND_BOOTS, Items.DIAMOND_HELMET);
    
        weapons.addEntries(Rarity.COMMON, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(Items.STONE_SWORD, Rarity.UNCOMMON);
        weapons.addEntry(Items.IRON_SWORD, Rarity.RARE);
    
        consumables.addEntries(Rarity.COMMON, Items.EGG_OF_DOOM, Items.SLOWBALL);
        consumables.addEntries(Rarity.UNCOMMON, Items.COBWEB, Items.XP_BOTTLE);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, Rarity.RARE));
        consumables.addEntries(Rarity.RARE, Items.TNT, Items.WET_NODDLE);
        consumables.addEntry(Items.ENDER_PEARL, Rarity.EPIC);
    
        utilities.addEntries(Rarity.COMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(Rarity.COMMON, Items.GOLD_INGOT, Items.STICK);
        components.addEntry(Items.IRON_INGOT, Rarity.UNCOMMON);
        components.addEntry(Items.DIAMOND, Rarity.EPIC);
    }
}
