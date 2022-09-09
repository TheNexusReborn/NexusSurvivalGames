package com.thenexusreborn.survivalgames.loot.v2;

import com.thenexusreborn.nexuscore.util.MaterialNames;
import org.bukkit.Material;

import java.util.*;

public class LootManager {
    
    private final List<LootTable> lootTables = new ArrayList<>();
    
    private static final LootManager instance = new LootManager();
    
    public static LootManager getInstance() {
        return instance;
    }
    
    public LootManager() {
        LootTable tierOne = new LootTable("tierOne");
        this.lootTables.add(tierOne);
        
        LootCategory food = new LootCategory("food", Rarity.COMMON);
        LootCategory armor = new LootCategory("armor", Rarity.COMMON);
        LootCategory weapons = new LootCategory("weapons", Rarity.COMMON);
        LootCategory consumables = new LootCategory("consumables", Rarity.UNCOMMON);
        LootCategory utilities = new LootCategory("utilities", Rarity.RARE);
        LootCategory components = new LootCategory("components", Rarity.UNCOMMON);
        LootCategory legendary = new LootCategory("legendary", Rarity.LEGENDARY);
        
        tierOne.addCategory(food);
        tierOne.addCategory(armor);
        tierOne.addCategory(weapons);
        tierOne.addCategory(consumables);
        tierOne.addCategory(utilities);
        tierOne.addCategory(components);
        tierOne.addCategory(legendary);
        
        food.addEntry(new LootEntry(Material.GRILLED_PORK, "Porkchop", Rarity.RARE));
        food.addEntry(new LootEntry(Material.COOKED_BEEF, "Steak", Rarity.RARE));
        food.addEntry(new LootEntry(Material.COOKED_CHICKEN, "Grilled Chicken", Rarity.RARE));
        food.addEntry(new LootEntry(Material.PORK, "Raw Porkchop", Rarity.COMMON));
        food.addEntry(new LootEntry(Material.RAW_BEEF, Rarity.COMMON));
        food.addEntry(new LootEntry(Material.RAW_CHICKEN, Rarity.UNCOMMON));
        food.addEntry(new LootEntry(Material.CARROT_ITEM, "Carrot", Rarity.COMMON));
        food.addEntry(new LootEntry(Material.POTATO_ITEM, "Potato", Rarity.COMMON));
        food.addEntry(new LootEntry(Material.BAKED_POTATO, Rarity.UNCOMMON));
        food.addEntry(new LootEntry(Material.CAKE, Rarity.RARE));
        food.addEntry(new LootEntry(Material.PUMPKIN_PIE, Rarity.RARE));
        food.addEntry(new LootEntry(Material.RAW_FISH, Rarity.COMMON));
        food.addEntry(new LootEntry(Material.COOKED_FISH, "Vile Creature", Rarity.RARE));
        food.addEntry(new LootEntry(Material.GOLDEN_CARROT, Rarity.EPIC));
        food.addEntry(new LootEntry(Material.APPLE, Rarity.UNCOMMON));
        food.addEntry(new LootEntry(Material.MELON, Rarity.COMMON));
        
        armor.addEntry(new LootEntry(Material.LEATHER_HELMET, Rarity.COMMON));
        armor.addEntry(new LootEntry(Material.LEATHER_CHESTPLATE, Rarity.COMMON));
        armor.addEntry(new LootEntry(Material.LEATHER_LEGGINGS, Rarity.COMMON));
        armor.addEntry(new LootEntry(Material.LEATHER_BOOTS, Rarity.COMMON));
        armor.addEntry(new LootEntry(Material.GOLD_HELMET, "The Crown" ,Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.GOLD_CHESTPLATE, Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.GOLD_LEGGINGS, Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.GOLD_BOOTS, Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.CHAINMAIL_HELMET, "Lingerie Helmet", Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.CHAINMAIL_CHESTPLATE, "Lingerie Chestplate", Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.CHAINMAIL_LEGGINGS, "Lingerie Leggings", Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.CHAINMAIL_BOOTS, "Lingerie Boots", Rarity.UNCOMMON));
        armor.addEntry(new LootEntry(Material.IRON_HELMET, Rarity.RARE));
        armor.addEntry(new LootEntry(Material.IRON_CHESTPLATE, Rarity.RARE));
        armor.addEntry(new LootEntry(Material.IRON_LEGGINGS, Rarity.RARE));
        armor.addEntry(new LootEntry(Material.IRON_BOOTS, Rarity.RARE));
        
        weapons.addEntry(new LootEntry(Material.WOOD_AXE, Rarity.COMMON));
        weapons.addEntry(new LootEntry(Material.WOOD_SWORD, Rarity.UNCOMMON));
        weapons.addEntry(new LootEntry(Material.STONE_AXE, Rarity.UNCOMMON));
        weapons.addEntry(new LootEntry(Material.STONE_SWORD, Rarity.RARE));
        weapons.addEntry(new LootEntry(Material.BOW, Rarity.UNCOMMON));
        
        consumables.addEntry(new LootEntry(Material.ARROW, MaterialNames.getDefaultName(Material.ARROW), Rarity.RARE, 5, new ArrayList<>()));
        consumables.addEntry(new LootEntry(Material.EGG, "Egg of Doom", Rarity.UNCOMMON));
        consumables.addEntry(new LootEntry(Material.SNOW_BALL, "Slowball", Rarity.UNCOMMON));
        consumables.addEntry(new LootEntry(Material.TNT, "TNT", Rarity.RARE));
        consumables.addEntry(new LootEntry(Material.WEB, "Cobweb", Rarity.UNCOMMON));
        consumables.addEntry(new LootEntry(Material.EXP_BOTTLE, "XP Bottle", Rarity.EPIC));
        consumables.addEntry(new LootEntry(Material.ROTTEN_FLESH, "Wet Noodle", Rarity.RARE));
        
        utilities.addEntry(new LootEntry(Material.COMPASS, "Player Tracker", Rarity.UNCOMMON));
        utilities.addEntry(new LootEntry(Material.FLINT_AND_STEEL, Rarity.UNCOMMON));
        utilities.addEntry(new LootEntry(Material.FISHING_ROD, Rarity.RARE));
        
        components.addEntry(new LootEntry(Material.IRON_INGOT, Rarity.EPIC));
        components.addEntry(new LootEntry(Material.GOLD_INGOT, Rarity.RARE));
        components.addEntry(new LootEntry(Material.STICK, Rarity.UNCOMMON));
        components.addEntry(new LootEntry(Material.FEATHER, Rarity.COMMON));
        components.addEntry(new LootEntry(Material.FLINT, Rarity.COMMON));
    
        legendary.addEntry(new LootEntry(Material.GOLDEN_APPLE, "Golden Munchie", Rarity.LEGENDARY));
        legendary.addEntry(new LootEntry(Material.DIAMOND, Rarity.LEGENDARY));
        legendary.addEntry(new LootEntry(Material.ENDER_PEARL, Rarity.LEGENDARY));
    }
    
    public List<LootTable> getLootTables() {
        return lootTables;
    }
    
    public LootTable getLootTable(String name) {
        for (LootTable lootTable : this.lootTables) {
            if (lootTable.getName().equalsIgnoreCase(name)) {
                return lootTable;
            }
        }
        
        return null;
    }
}
