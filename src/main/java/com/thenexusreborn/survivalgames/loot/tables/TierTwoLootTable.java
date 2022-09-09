package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierTwoLootTable extends LootTable {
    public TierTwoLootTable() {
        super("tierTwo");
    
        LootCategory food = new LootCategory("food", Rarity.COMMON);
        LootCategory armor = new LootCategory("armor", Rarity.COMMON);
        LootCategory weapons = new LootCategory("weapons", Rarity.COMMON);
        LootCategory consumables = new LootCategory("consumables", Rarity.UNCOMMON);
        LootCategory utilities = new LootCategory("utilities", Rarity.RARE);
        LootCategory components = new LootCategory("components", Rarity.UNCOMMON);
    
        addCategory(food);
        addCategory(armor);
        addCategory(weapons);
        addCategory(consumables);
        addCategory(utilities);
        addCategory(components);
    
        food.addEntries(Rarity.UNCOMMON, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE, Items.VILE_CREATURE);
        food.addEntries(Rarity.COMMON, Items.BAKED_POTATO, Items.APPLE);
        food.addEntry(Items.GOLDEN_CARROT, Rarity.RARE);
        food.addEntry(Items.GOLDEN_MUNCHIE, Rarity.EPIC);
    
        armor.addEntries(Rarity.COMMON, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(Rarity.UNCOMMON, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
        armor.addEntries(Rarity.LEGENDARY, Items.DIAMOND_HELMET, Items.DIAMOND_BOOTS);
    
        weapons.addEntries(Rarity.COMMON, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(Items.STONE_SWORD, Rarity.UNCOMMON);
        weapons.addEntry(Items.IRON_SWORD, Rarity.RARE);
    
        consumables.addEntries(Rarity.COMMON, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.COBWEB);
        consumables.addEntry(new LootEntry(Items.ARROW, 10, Rarity.UNCOMMON));
        consumables.addEntries(Rarity.UNCOMMON, Items.TNT, Items.WET_NODDLE);
        consumables.addEntry(Items.XP_BOTTLE, Rarity.RARE);
        consumables.addEntry(Items.ENDER_PEARL, Rarity.EPIC);
    
        utilities.addEntries(Rarity.UNCOMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntry(Items.IRON_INGOT, Rarity.RARE);
        components.addEntries(Rarity.UNCOMMON, Items.GOLD_INGOT, Items.STICK);
        components.addEntries(Rarity.COMMON, Items.FEATHER, Items.FLINT);
        components.addEntry(Items.DIAMOND, Rarity.EPIC);
    }
}
