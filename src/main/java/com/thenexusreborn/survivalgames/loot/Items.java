package com.thenexusreborn.survivalgames.loot;

import org.bukkit.Material;

import java.util.*;

public final class Items {
    public static final ItemRegistry REGISTRY = new ItemRegistry();
    
    public static final LootItem PORKCHOP = new LootItem(Categories.COOKED_FOOD, Material.GRILLED_PORK, "Porkchop");
    public static final LootItem STEAK = new LootItem(Categories.COOKED_FOOD, Material.COOKED_BEEF, "Steak");
    public static final LootItem GRILLED_CHICKEN = new LootItem(Categories.RAW_FOOD, Material.COOKED_CHICKEN, "Grilled Chicken");
    public static final LootItem RAW_PORKCHOP = new LootItem(Categories.RAW_FOOD, Material.PORK, "Raw Porkchop");
    public static final LootItem RAW_BEEF = new LootItem(Categories.RAW_FOOD, Material.RAW_BEEF);
    public static final LootItem RAW_CHICKEN = new LootItem(Categories.RAW_FOOD, Material.RAW_CHICKEN);
    public static final LootItem CARROT = new LootItem(Categories.MISC_FOOD, Material.CARROT_ITEM, "Carrot");
    public static final LootItem POTATO = new LootItem(Categories.MISC_FOOD, Material.POTATO_ITEM, "Potato");
    public static final LootItem BAKED_POTATO = new LootItem(Categories.COOKED_FOOD, Material.BAKED_POTATO);
    public static final LootItem CAKE = new LootItem(Categories.MISC_FOOD, Material.CAKE);
    public static final LootItem PUMPKIN_PIE = new LootItem(Categories.COOKED_FOOD, Material.PUMPKIN_PIE);
    public static final LootItem RAW_FISH = new LootItem(Categories.RAW_FOOD, Material.RAW_FISH);
    public static final LootItem VILE_CREATURE = new LootItem(Categories.COOKED_FOOD, Material.COOKED_FISH, "Vile Creature");
    public static final LootItem GOLDEN_CARROT = new LootItem(Categories.MISC_FOOD, Material.GOLDEN_CARROT);
    public static final LootItem APPLE = new LootItem(Categories.MISC_FOOD, Material.APPLE);
    public static final LootItem MELON = new LootItem(Categories.MISC_FOOD, Material.MELON, "turq Melon");
    public static final LootItem GOLDEN_MUNCHIE = new LootItem(Categories.MISC_FOOD, Material.GOLDEN_APPLE, "Golden Munchie");
    public static final LootItem COOKIE = new LootItem(Categories.MISC_FOOD, Material.COOKIE);
    public static final LootItem MUSHROOM_SOUP = new LootItem(Categories.MISC_FOOD, Material.MUSHROOM_SOUP);
    
    public static final LootItem LEATHER_HELMET = new LootItem(Categories.ARMOR, Material.LEATHER_HELMET);
    public static final LootItem LEATHER_CHESTPLATE = new LootItem(Categories.ARMOR, Material.LEATHER_CHESTPLATE);
    public static final LootItem LEATHER_LEGGINGS = new LootItem(Categories.ARMOR, Material.LEATHER_LEGGINGS);
    public static final LootItem LEATHER_BOOTS = new LootItem(Categories.ARMOR, Material.LEATHER_BOOTS);
    public static final LootItem THE_CROWN = new LootItem(Categories.ARMOR, Material.GOLD_HELMET, "&6The Crown");
    public static final LootItem GOLD_CHESTPLATE = new LootItem(Categories.ARMOR, Material.GOLD_CHESTPLATE);
    public static final LootItem GOLD_LEGGINGS = new LootItem(Categories.ARMOR, Material.GOLD_LEGGINGS);
    public static final LootItem GOLD_BOOTS = new LootItem(Categories.ARMOR, Material.GOLD_BOOTS);
    public static final LootItem LINGERIE_HELMET = new LootItem(Categories.ARMOR, Material.CHAINMAIL_HELMET, "Lingerie Helmet");
    public static final LootItem LINGERIE_CHESTPLATE = new LootItem(Categories.ARMOR, Material.CHAINMAIL_CHESTPLATE, "Lingerie Chestplate");
    public static final LootItem LINGERIE_LEGGINGS = new LootItem(Categories.ARMOR, Material.CHAINMAIL_LEGGINGS, "Lingerie Leggings");
    public static final LootItem LINGERIE_BOOTS = new LootItem(Categories.ARMOR, Material.CHAINMAIL_BOOTS, "Lingerie Boots");
    public static final LootItem IRON_HELMET = new LootItem(Categories.ARMOR, Material.IRON_HELMET);
    public static final LootItem IRON_CHESTPLATE = new LootItem(Categories.ARMOR, Material.IRON_CHESTPLATE);
    public static final LootItem IRON_LEGGINGS = new LootItem(Categories.ARMOR, Material.IRON_LEGGINGS);
    public static final LootItem IRON_BOOTS = new LootItem(Categories.ARMOR, Material.IRON_BOOTS);
    
    public static final LootItem WOOD_AXE = new LootItem(Categories.WEAPONS, Material.WOOD_AXE);
    public static final LootItem WOOD_SWORD = new LootItem(Categories.WEAPONS, Material.WOOD_SWORD);
    public static final LootItem STONE_AXE = new LootItem(Categories.WEAPONS, Material.STONE_AXE);
    public static final LootItem STONE_SWORD = new LootItem(Categories.WEAPONS, Material.STONE_SWORD);
    public static final LootItem IRON_SWORD = new LootItem(Categories.WEAPONS, Material.IRON_SWORD);
    public static final LootItem BOW = new LootItem(Categories.WEAPONS, Material.BOW);
    public static final LootItem ARROW = new LootItem(Categories.WEAPONS, Material.ARROW);
    
    public static final LootItem EGG_OF_DOOM = new LootItem(Categories.THROWABLES, Material.EGG, "Egg of Doom &8- &7Hunger and Confusion");
    public static final LootItem SLOWBALL = new LootItem(Categories.THROWABLES, Material.SNOW_BALL, "Slowball &8- &7Slowness");
    public static final LootItem TNT = new LootItem(Categories.PLACEABLES, Material.TNT, "TNT");
    public static final LootItem COBWEB = new LootItem(Categories.PLACEABLES, Material.WEB, "Cobweb");
    public static final LootItem XP_BOTTLE = new LootItem(Categories.THROWABLES, Material.EXP_BOTTLE, "XP Bottle");
    public static final LootItem WET_NODDLE = new LootItem(Categories.MISC_FOOD, Material.ROTTEN_FLESH, "Wet Noodle", Collections.singletonList("&7&oEat this to get a 15% chance to get Strength II"));
    public static final LootItem ENDER_PEARL = new LootItem(Categories.THROWABLES, Material.ENDER_PEARL);
    
    public static final LootItem PLAYER_TRACKER = new LootItem(Categories.TOOLS, Material.COMPASS, "Player Tracker");
    public static final LootItem FLINT_AND_STEEL = new LootItem(Categories.TOOLS, Material.FLINT_AND_STEEL);
    public static final LootItem FISHING_ROD = new LootItem(Categories.TOOLS, Material.FISHING_ROD);
    
    public static final LootItem IRON_INGOT = new LootItem(Categories.COMPONENTS, Material.IRON_INGOT);
    public static final LootItem GOLD_INGOT = new LootItem(Categories.COMPONENTS, Material.GOLD_INGOT);
    public static final LootItem STICK = new LootItem(Categories.COMPONENTS, Material.STICK);
    public static final LootItem FEATHER = new LootItem(Categories.COMPONENTS, Material.FEATHER);
    public static final LootItem FLINT = new LootItem(Categories.COMPONENTS, Material.FLINT);
    public static final LootItem DIAMOND = new LootItem(Categories.COMPONENTS, Material.DIAMOND);
}