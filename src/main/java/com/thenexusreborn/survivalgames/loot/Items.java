package com.thenexusreborn.survivalgames.loot;

import org.bukkit.Material;

import java.util.*;

public final class Items {
    public static final List<LootItem> REGISTRY = new ArrayList<>();
    
    public static final LootItem PORKCHOP = new LootItem(Material.GRILLED_PORK, "Porkchop");
    public static final LootItem STEAK = new LootItem(Material.COOKED_BEEF, "Steak");
    public static final LootItem GRILLED_CHICKEN = new LootItem(Material.COOKED_CHICKEN, "Grilled Chicken");
    public static final LootItem RAW_PORKCHOP = new LootItem(Material.PORK, "Raw Porkchop");
    public static final LootItem RAW_BEEF = new LootItem(Material.RAW_BEEF);
    public static final LootItem RAW_CHICKEN = new LootItem(Material.RAW_CHICKEN);
    public static final LootItem CARROT = new LootItem(Material.CARROT_ITEM, "Carrot");
    public static final LootItem POTATO = new LootItem(Material.POTATO_ITEM, "Potato");
    public static final LootItem BAKED_POTATO = new LootItem(Material.BAKED_POTATO);
    public static final LootItem CAKE = new LootItem(Material.CAKE);
    public static final LootItem PUMPKIN_PIE = new LootItem(Material.PUMPKIN_PIE);
    public static final LootItem RAW_FISH = new LootItem(Material.RAW_FISH);
    public static final LootItem VILE_CREATURE = new LootItem(Material.COOKED_FISH, "Vile Creature");
    public static final LootItem GOLDEN_CARROT = new LootItem(Material.GOLDEN_CARROT);
    public static final LootItem APPLE = new LootItem(Material.APPLE);
    public static final LootItem MELON = new LootItem(Material.MELON);
    public static final LootItem GOLDEN_MUNCHIE = new LootItem(Material.GOLDEN_APPLE, "Golden Munchie");
    public static final LootItem COOKIE = new LootItem(Material.COOKIE);
    
    public static final LootItem LEATHER_HELMET = new LootItem(Material.LEATHER_HELMET);
    public static final LootItem LEATHER_CHESTPLATE = new LootItem(Material.LEATHER_CHESTPLATE);
    public static final LootItem LEATHER_LEGGINGS = new LootItem(Material.LEATHER_LEGGINGS);
    public static final LootItem LEATHER_BOOTS = new LootItem(Material.LEATHER_BOOTS);
    public static final LootItem THE_CROWN = new LootItem(Material.GOLD_HELMET, "&6The Crown");
    public static final LootItem GOLD_CHESTPLATE = new LootItem(Material.GOLD_CHESTPLATE);
    public static final LootItem GOLD_LEGGINGS = new LootItem(Material.GOLD_LEGGINGS);
    public static final LootItem GOLD_BOOTS = new LootItem(Material.GOLD_BOOTS);
    public static final LootItem LINGERIE_HELMET = new LootItem(Material.CHAINMAIL_HELMET, "Lingerie Helmet");
    public static final LootItem LINGERIE_CHESTPLATE = new LootItem(Material.CHAINMAIL_CHESTPLATE, "Lingerie Chestplate");
    public static final LootItem LINGERIE_LEGGINGS = new LootItem(Material.CHAINMAIL_LEGGINGS, "Lingerie Leggings");
    public static final LootItem LINGERIE_BOOTS = new LootItem(Material.CHAINMAIL_BOOTS, "Lingerie Boots");
    public static final LootItem IRON_HELMET = new LootItem(Material.IRON_HELMET);
    public static final LootItem IRON_CHESTPLATE = new LootItem(Material.IRON_CHESTPLATE);
    public static final LootItem IRON_LEGGINGS = new LootItem(Material.IRON_LEGGINGS);
    public static final LootItem IRON_BOOTS = new LootItem(Material.IRON_BOOTS);
    
    public static final LootItem WOOD_AXE = new LootItem(Material.WOOD_AXE);
    public static final LootItem WOOD_SWORD = new LootItem(Material.WOOD_SWORD);
    public static final LootItem STONE_AXE = new LootItem(Material.STONE_AXE);
    public static final LootItem STONE_SWORD = new LootItem(Material.STONE_SWORD);
    public static final LootItem IRON_SWORD = new LootItem(Material.IRON_SWORD);
    public static final LootItem BOW = new LootItem(Material.BOW);
    
    public static final LootItem ARROW = new LootItem(Material.ARROW);
    public static final LootItem EGG_OF_DOOM = new LootItem(Material.EGG, "Egg of Doom &8- &7Hunger and Confusion");
    public static final LootItem SLOWBALL = new LootItem(Material.SNOW_BALL, "Slowball &8- &7Slowness");
    public static final LootItem TNT = new LootItem(Material.TNT, "TNT");
    public static final LootItem COBWEB = new LootItem(Material.WEB, "Cobweb");
    public static final LootItem XP_BOTTLE = new LootItem(Material.EXP_BOTTLE, "XP Bottle");
    public static final LootItem WET_NODDLE = new LootItem(Material.ROTTEN_FLESH, "Wet Noodle", Collections.singletonList("&7&oEat this to get a 15% chance to get Strength II"));
    public static final LootItem ENDER_PEARL = new LootItem(Material.ENDER_PEARL);
    
    public static final LootItem PLAYER_TRACKER = new LootItem(Material.COMPASS, "Player Tracker");
    public static final LootItem FLINT_AND_STEEL = new LootItem(Material.FLINT_AND_STEEL);
    public static final LootItem FISHING_ROD = new LootItem(Material.FISHING_ROD);
    
    public static final LootItem IRON_INGOT = new LootItem(Material.IRON_INGOT);
    public static final LootItem GOLD_INGOT = new LootItem(Material.GOLD_INGOT);
    public static final LootItem STICK = new LootItem(Material.STICK);
    public static final LootItem FEATHER = new LootItem(Material.FEATHER);
    public static final LootItem FLINT = new LootItem(Material.FLINT);
    public static final LootItem DIAMOND = new LootItem(Material.DIAMOND);
}