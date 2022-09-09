package com.thenexusreborn.survivalgames.loot.v2.tables;

import com.thenexusreborn.survivalgames.loot.v2.*;

public class TierOneLootTable extends LootTable {
    public TierOneLootTable() {
        super("tierOne");
    
        LootCategory food = new LootCategory("food", Rarity.COMMON);
        LootCategory armor = new LootCategory("armor", Rarity.COMMON);
        LootCategory weapons = new LootCategory("weapons", Rarity.COMMON);
        LootCategory consumables = new LootCategory("consumables", Rarity.UNCOMMON);
        LootCategory utilities = new LootCategory("utilities", Rarity.RARE);
        LootCategory components = new LootCategory("components", Rarity.UNCOMMON);
        LootCategory legendary = new LootCategory("legendary", Rarity.LEGENDARY);
    
        addCategory(food);
        addCategory(armor);
        addCategory(weapons);
        addCategory(consumables);
        addCategory(utilities);
        addCategory(components);
        addCategory(legendary);
    
        food.addEntries(Rarity.RARE, Items.PORKCHOP, Items.STEAK, Items.GRILLED_CHICKEN, Items.CAKE, Items.PUMPKIN_PIE, Items.VILE_CREATURE);
        food.addEntries(Rarity.COMMON, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.CARROT, Items.POTATO, Items.RAW_FISH, Items.MELON);
        food.addEntries(Rarity.UNCOMMON, Items.RAW_CHICKEN, Items.BAKED_POTATO, Items.APPLE);
        food.addEntry(Items.GOLDEN_CARROT, Rarity.EPIC);
    
        armor.addEntries(Rarity.COMMON, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
        armor.addEntries(Rarity.UNCOMMON, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        armor.addEntries(Rarity.RARE, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    
        weapons.addEntry(Items.WOOD_AXE, Rarity.COMMON);
        weapons.addEntries(Rarity.UNCOMMON, Items.WOOD_SWORD, Items.STONE_AXE, Items.BOW);
        weapons.addEntry(Items.STONE_SWORD, Rarity.RARE);
    
        consumables.addEntries(Rarity.UNCOMMON, Items.EGG_OF_DOOM, Items.SLOWBALL, Items.COBWEB);
        consumables.addEntry(new LootEntry(Items.ARROW, 5, Rarity.RARE));
        consumables.addEntries(Rarity.RARE, Items.TNT, Items.WET_NODDLE);
        consumables.addEntry(Items.XP_BOTTLE, Rarity.EPIC);
    
        utilities.addEntries(Rarity.UNCOMMON, Items.PLAYER_TRACKER, Items.FLINT_AND_STEEL);
        utilities.addEntry(Items.FISHING_ROD, Rarity.RARE);
    
        components.addEntry(Items.IRON_INGOT, Rarity.RARE);
        components.addEntries(Rarity.UNCOMMON, Items.GOLD_INGOT, Items.STICK);
        components.addEntries(Rarity.COMMON, Items.FEATHER, Items.FLINT);
    
        legendary.addEntries(Rarity.LEGENDARY, Items.GOLDEN_MUNCHIE, Items.DIAMOND, Items.ENDER_PEARL);
    }
}
