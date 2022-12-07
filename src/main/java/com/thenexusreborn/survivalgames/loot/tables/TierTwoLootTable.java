package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.loot.*;

public class TierTwoLootTable extends LootTable {
    public TierTwoLootTable() {
        super("tierTwo");
    
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
    
        food.addEntries(Rarity.COMMON, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.RAW_FISH);
        food.addEntries(Rarity.UNCOMMON, Items.BAKED_POTATO, Items.APPLE);
        food.addEntries(Rarity.RARE, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE, Items.VILE_CREATURE);
        food.addEntry(Rarity.EPIC, Items.GOLDEN_CARROT);
        food.addEntry(Rarity.LEGENDARY, Items.GOLDEN_MUNCHIE);
    
        armor.addEntries(Rarity.COMMON, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS);
        armor.addEntries(Rarity.UNCOMMON, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(Rarity.RARE, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntry(Rarity.COMMON, Items.WOOD_SWORD);
        weapons.addEntries(Rarity.UNCOMMON, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(Rarity.RARE, Items.STONE_SWORD);
    
        consumables.addEntries(Rarity.UNCOMMON, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.COBWEB);
        consumables.addEntry(new LootEntry(Items.ARROW, 5, Rarity.RARE.getMax()));
        consumables.addEntries(Rarity.RARE, Items.TNT, Items.WET_NODDLE, Items.XP_BOTTLE);
        consumables.addEntry(Rarity.LEGENDARY, Items.ENDER_PEARL);
    
        utilities.addEntries(Rarity.COMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL, Items.FISHING_ROD);
    
        components.addEntries(Rarity.COMMON, Items.FEATHER, Items.FLINT, Items.STICK, Items.GOLD_INGOT);
        components.addEntries(Rarity.UNCOMMON, Items.IRON_INGOT);
        components.addEntry(Rarity.RARE, Items.DIAMOND);
    }
}
