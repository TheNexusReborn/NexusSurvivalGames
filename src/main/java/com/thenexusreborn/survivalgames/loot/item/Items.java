package com.thenexusreborn.survivalgames.loot.item;

import org.bukkit.Material;

import static com.thenexusreborn.survivalgames.loot.category.LootCategory.*;

public final class Items {
    public static final ItemRegistry REGISTRY = new ItemRegistry();
    
    public static final LootItem PORKCHOP = REGISTRY.register("Porkchop", Material.GRILLED_PORK).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem STEAK = REGISTRY.register("Steak", Material.COOKED_BEEF).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem GRILLED_CHICKEN = REGISTRY.register("Grilled Chicken", Material.COOKED_CHICKEN).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem RAW_PORKCHOP = REGISTRY.register("Raw Porkchop", Material.PORK).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem RAW_BEEF = REGISTRY.register(Material.RAW_BEEF).setCategories(FOOD, RAW_FOOD);
    public static final LootItem RAW_CHICKEN = REGISTRY.register(Material.RAW_CHICKEN).setCategories(FOOD, RAW_FOOD);
    public static final LootItem CARROT = REGISTRY.register("Carrot", Material.CARROT_ITEM).setCategories(FOOD, MISC_FOOD);
    public static final LootItem POTATO = REGISTRY.register(Material.POTATO_ITEM).setCategories(FOOD, MISC_FOOD);
    public static final LootItem BAKED_POTATO = REGISTRY.register(Material.BAKED_POTATO).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem CAKE = REGISTRY.register(Material.CAKE).setCategories(FOOD, MISC_FOOD);
    public static final LootItem PUMPKIN_PIE = REGISTRY.register(Material.PUMPKIN_PIE).setCategories(FOOD, MISC_FOOD);
    public static final LootItem RAW_FISH = REGISTRY.register(Material.RAW_FISH).setCategories(FOOD, RAW_FOOD);
    public static final LootItem VILE_CREATURE = REGISTRY.register("Vile Creature", Material.COOKED_FISH).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem GOLDEN_CARROT = REGISTRY.register(Material.GOLDEN_CARROT).setCategories(FOOD, MISC_FOOD);
    public static final LootItem APPLE = REGISTRY.register(Material.APPLE).setCategories(FOOD, MISC_FOOD);
    public static final LootItem MELON = REGISTRY.register("turqMelon", Material.MELON).setCategories(FOOD, MISC_FOOD);
    public static final LootItem GOLDEN_MUNCHIE = REGISTRY.register("Golden Munchie", Material.GOLDEN_APPLE).setCategories(FOOD, COOKED_FOOD);
    public static final LootItem COOKIE = REGISTRY.register(Material.COOKIE).setCategories(FOOD, MISC_FOOD);
    public static final LootItem MUSHROOM_SOUP = REGISTRY.register(Material.MUSHROOM_SOUP).setCategories(FOOD, COOKED_FOOD);
    
    public static final LootItem LEATHER_HELMET = REGISTRY.register(Material.LEATHER_HELMET).setCategories(ARMOR, LEATHER_ARMOR, HELMETS);
    public static final LootItem LEATHER_CHESTPLATE = REGISTRY.register(Material.LEATHER_CHESTPLATE).setCategories(ARMOR, LEATHER_ARMOR, CHESTPLATES);
    public static final LootItem LEATHER_LEGGINGS = REGISTRY.register(Material.LEATHER_LEGGINGS).setCategories(ARMOR, LEATHER_ARMOR, LEGGINGS);
    public static final LootItem LEATHER_BOOTS = REGISTRY.register(Material.LEATHER_BOOTS).setCategories(ARMOR, LEATHER_ARMOR, BOOTS);
    public static final LootItem THE_CROWN = REGISTRY.register("&6The Crown", Material.GOLD_HELMET).setCategories(ARMOR, GOLD_ARMOR, HELMETS);
    public static final LootItem GOLD_CHESTPLATE = REGISTRY.register(Material.GOLD_CHESTPLATE).setCategories(ARMOR, GOLD_ARMOR, CHESTPLATES);
    public static final LootItem GOLD_LEGGINGS = REGISTRY.register(Material.GOLD_LEGGINGS).setCategories(ARMOR, GOLD_ARMOR, LEGGINGS);
    public static final LootItem GOLD_BOOTS = REGISTRY.register(Material.GOLD_BOOTS).setCategories(ARMOR, GOLD_ARMOR, BOOTS);
    public static final LootItem LINGERIE_HELMET = REGISTRY.register("Lingerie Helmet", Material.CHAINMAIL_HELMET).setCategories(ARMOR, CHAINMAIL_ARMOR, HELMETS);
    public static final LootItem LINGERIE_CHESTPLATE = REGISTRY.register("Lingerie Chestplate", Material.CHAINMAIL_CHESTPLATE).setCategories(ARMOR, CHAINMAIL_ARMOR, CHESTPLATES);
    public static final LootItem LINGERIE_LEGGINGS = REGISTRY.register("Lingerie Leggings", Material.CHAINMAIL_LEGGINGS).setCategories(ARMOR, CHAINMAIL_ARMOR, LEGGINGS);
    public static final LootItem LINGERIE_BOOTS = REGISTRY.register("Lingerie Boots", Material.CHAINMAIL_BOOTS).setCategories(ARMOR, CHAINMAIL_ARMOR, BOOTS);
    public static final LootItem IRON_HELMET = REGISTRY.register(Material.IRON_HELMET).setCategories(ARMOR, IRON_ARMOR, HELMETS);
    public static final LootItem IRON_CHESTPLATE = REGISTRY.register(Material.IRON_CHESTPLATE).setCategories(ARMOR, IRON_ARMOR, CHESTPLATES);
    public static final LootItem IRON_LEGGINGS = REGISTRY.register(Material.IRON_LEGGINGS).setCategories(ARMOR, IRON_ARMOR, LEGGINGS);
    public static final LootItem IRON_BOOTS = REGISTRY.register(Material.IRON_BOOTS).setCategories(ARMOR, IRON_ARMOR, BOOTS);
    public static final LootItem DIAMOND_HELMET = REGISTRY.register(Material.DIAMOND_HELMET).setCategories(ARMOR, DIAMOND_ARMOR, HELMETS);
    public static final LootItem DIAMOND_CHESTPLATE = REGISTRY.register(Material.DIAMOND_CHESTPLATE).setCategories(ARMOR, DIAMOND_ARMOR, CHESTPLATES);
    public static final LootItem DIAMOND_LEGGINGS = REGISTRY.register(Material.DIAMOND_LEGGINGS).setCategories(ARMOR, DIAMOND_ARMOR, LEGGINGS);
    public static final LootItem DIAMOND_BOOTS = REGISTRY.register(Material.DIAMOND_BOOTS).setCategories(ARMOR, DIAMOND_ARMOR, BOOTS);
    
    public static final LootItem WOOD_AXE = REGISTRY.register(Material.WOOD_AXE).setCategories(WEAPONS, WOODEN_WEAPONS, AXES);
    public static final LootItem WOOD_SWORD = REGISTRY.register(Material.WOOD_SWORD).setCategories(WEAPONS, WOODEN_WEAPONS, SWORDS);
    public static final LootItem SACRIFICIAL_AXE = REGISTRY.register("&4Sacrificial Axe", Material.GOLD_AXE).setCategories(WEAPONS, GOLD_WEAPONS, AXES);
    public static final LootItem SACRIFICIAL_SWORD = REGISTRY.register("&4Sacrificial Sword", Material.GOLD_SWORD).setCategories(WEAPONS, GOLD_WEAPONS, SWORDS);
    public static final LootItem STONE_AXE = REGISTRY.register(Material.STONE_AXE).setCategories(WEAPONS, STONE_WEAPONS, AXES);
    public static final LootItem STONE_SWORD = REGISTRY.register(Material.STONE_SWORD).setCategories(WEAPONS, STONE_WEAPONS, SWORDS);
    public static final LootItem FREDERICK = REGISTRY.register("Frederick", Material.IRON_AXE).setCategories(WEAPONS, IRON_WEAPONS, AXES);
    public static final LootItem IRON_SWORD = REGISTRY.register(Material.IRON_SWORD).setCategories(WEAPONS, IRON_WEAPONS, SWORDS);
    public static final LootItem BETTY = REGISTRY.register("&bBetty", Material.DIAMOND_AXE).setCategories(WEAPONS, DIAMOND_WEAPONS, AXES);
    public static final LootItem DIAMOND_SWORD = REGISTRY.register(Material.DIAMOND_SWORD).setCategories(WEAPONS, DIAMOND_WEAPONS, SWORDS);
    public static final LootItem BOW = REGISTRY.register(Material.BOW).setCategories(WEAPONS, RANGED);
    public static final LootItem ARROW = REGISTRY.register(Material.ARROW).setCategories(WEAPONS, RANGED).setAmount(3);
    
    public static final LootItem EGG_OF_DOOM = REGISTRY.register("Egg of Doom &8- &7Hunger and Confusion", Material.EGG).setCategories(THROWABLES);
    public static final LootItem SLOWBALL = REGISTRY.register("Slowball &8- &7Slowness", Material.SNOW_BALL).setCategories(THROWABLES);
    public static final LootItem TNT = REGISTRY.register("TNT", Material.TNT).setCategories(PLACEABLES);
    public static final LootItem COBWEB = REGISTRY.register("Cobweb", Material.WEB).setCategories(PLACEABLES);
    public static final LootItem XP_BOTTLE = REGISTRY.register("XP Bottle", Material.EXP_BOTTLE).setCategories(PLACEABLES);
    public static final LootItem WET_NODDLE = REGISTRY.register("Wet Noodle", Material.ROTTEN_FLESH).setCategories(FOOD, MISC_FOOD).setLore("&7&oEat this to get a 15% chance to get Strength II");
    public static final LootItem ENDER_PEARL = REGISTRY.register(Material.ENDER_PEARL).setCategories(THROWABLES);
    
    public static final LootItem PLAYER_TRACKER = REGISTRY.register("Player Tracker", Material.COMPASS).setCategories(TOOLS);
    public static final LootItem FLINT_AND_STEEL = REGISTRY.register(Material.FLINT_AND_STEEL).setCategories(TOOLS);
    public static final LootItem FISHING_ROD = REGISTRY.register(Material.FISHING_ROD).setCategories(TOOLS);
    
    public static final LootItem IRON_INGOT = REGISTRY.register(Material.IRON_INGOT).setCategories(COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT, ARMOR_COMPONENT);
    public static final LootItem GOLD_INGOT = REGISTRY.register(Material.GOLD_INGOT).setCategories(COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT);
    public static final LootItem STICK = REGISTRY.register(Material.STICK).setCategories(COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT);
    public static final LootItem FEATHER = REGISTRY.register(Material.FEATHER).setCategories(COMPONENTS, WEAPON_COMPONENT);
    public static final LootItem FLINT = REGISTRY.register(Material.FLINT).setCategories(COMPONENTS, TOOL_COMPONENT);
    public static final LootItem DIAMOND = REGISTRY.register(Material.DIAMOND).setCategories(COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT);
}