package com.thenexusreborn.survivalgames.loot.item;

import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import org.bukkit.Material;

import java.util.Collections;

public final class Items {
    public static final ItemRegistry REGISTRY = new ItemRegistry();
    
    public static final LootItem PORKCHOP = REGISTRY.register(new LootItem(LootCategory.COOKED_FOOD, Material.GRILLED_PORK, "Porkchop"));
    public static final LootItem STEAK = REGISTRY.register(new LootItem(LootCategory.COOKED_FOOD, Material.COOKED_BEEF, "Steak"));
    public static final LootItem GRILLED_CHICKEN = REGISTRY.register(new LootItem(LootCategory.RAW_FOOD, Material.COOKED_CHICKEN, "Grilled Chicken"));
    public static final LootItem RAW_PORKCHOP = REGISTRY.register(new LootItem(LootCategory.RAW_FOOD, Material.PORK, "Raw Porkchop"));
    public static final LootItem RAW_BEEF = REGISTRY.register(new LootItem(LootCategory.RAW_FOOD, Material.RAW_BEEF));
    public static final LootItem RAW_CHICKEN = REGISTRY.register(new LootItem(LootCategory.RAW_FOOD, Material.RAW_CHICKEN));
    public static final LootItem CARROT = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.CARROT_ITEM, "Carrot"));
    public static final LootItem POTATO = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.POTATO_ITEM, "Potato"));
    public static final LootItem BAKED_POTATO = REGISTRY.register(new LootItem(LootCategory.COOKED_FOOD, Material.BAKED_POTATO));
    public static final LootItem CAKE = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.CAKE));
    public static final LootItem PUMPKIN_PIE = REGISTRY.register(new LootItem(LootCategory.COOKED_FOOD, Material.PUMPKIN_PIE));
    public static final LootItem RAW_FISH = REGISTRY.register(new LootItem(LootCategory.RAW_FOOD, Material.RAW_FISH));
    public static final LootItem VILE_CREATURE = REGISTRY.register(new LootItem(LootCategory.COOKED_FOOD, Material.COOKED_FISH, "Vile Creature"));
    public static final LootItem GOLDEN_CARROT = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.GOLDEN_CARROT));
    public static final LootItem APPLE = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.APPLE));
    public static final LootItem MELON = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.MELON, "turqMelon"));
    public static final LootItem GOLDEN_MUNCHIE = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.GOLDEN_APPLE, "Golden Munchie"));
    public static final LootItem COOKIE = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.COOKIE));
    public static final LootItem MUSHROOM_SOUP = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.MUSHROOM_SOUP));
    
    public static final LootItem LEATHER_HELMET = REGISTRY.register(new LootItem(LootCategory.HELMETS, Material.LEATHER_HELMET));
    public static final LootItem LEATHER_CHESTPLATE = REGISTRY.register(new LootItem(LootCategory.CHESTPLATES, Material.LEATHER_CHESTPLATE));
    public static final LootItem LEATHER_LEGGINGS = REGISTRY.register(new LootItem(LootCategory.LEGGINGS, Material.LEATHER_LEGGINGS));
    public static final LootItem LEATHER_BOOTS = REGISTRY.register(new LootItem(LootCategory.BOOTS, Material.LEATHER_BOOTS));
    public static final LootItem THE_CROWN = REGISTRY.register(new LootItem(LootCategory.HELMETS, Material.GOLD_HELMET, "&6The Crown"));
    public static final LootItem GOLD_CHESTPLATE = REGISTRY.register(new LootItem(LootCategory.CHESTPLATES, Material.GOLD_CHESTPLATE));
    public static final LootItem GOLD_LEGGINGS = REGISTRY.register(new LootItem(LootCategory.LEGGINGS, Material.GOLD_LEGGINGS));
    public static final LootItem GOLD_BOOTS = REGISTRY.register(new LootItem(LootCategory.BOOTS, Material.GOLD_BOOTS));
    public static final LootItem LINGERIE_HELMET = REGISTRY.register(new LootItem(LootCategory.HELMETS, Material.CHAINMAIL_HELMET, "Lingerie Helmet"));
    public static final LootItem LINGERIE_CHESTPLATE = REGISTRY.register(new LootItem(LootCategory.CHESTPLATES, Material.CHAINMAIL_CHESTPLATE, "Lingerie Chestplate"));
    public static final LootItem LINGERIE_LEGGINGS = REGISTRY.register(new LootItem(LootCategory.LEGGINGS, Material.CHAINMAIL_LEGGINGS, "Lingerie Leggings"));
    public static final LootItem LINGERIE_BOOTS = REGISTRY.register(new LootItem(LootCategory.BOOTS, Material.CHAINMAIL_BOOTS, "Lingerie Boots"));
    public static final LootItem IRON_HELMET = REGISTRY.register(new LootItem(LootCategory.HELMETS, Material.IRON_HELMET));
    public static final LootItem IRON_CHESTPLATE = REGISTRY.register(new LootItem(LootCategory.CHESTPLATES, Material.IRON_CHESTPLATE));
    public static final LootItem IRON_LEGGINGS = REGISTRY.register(new LootItem(LootCategory.LEGGINGS, Material.IRON_LEGGINGS));
    public static final LootItem IRON_BOOTS = REGISTRY.register(new LootItem(LootCategory.BOOTS, Material.IRON_BOOTS));
    
    public static final LootItem WOOD_AXE = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.WOOD_AXE));
    public static final LootItem WOOD_SWORD = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.WOOD_SWORD));
    public static final LootItem STONE_AXE = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.STONE_AXE));
    public static final LootItem STONE_SWORD = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.STONE_SWORD));
    public static final LootItem IRON_SWORD = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.IRON_SWORD));
    public static final LootItem BOW = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.BOW));
    public static final LootItem ARROW = REGISTRY.register(new LootItem(LootCategory.WEAPONS, Material.ARROW, 5));
    
    public static final LootItem EGG_OF_DOOM = REGISTRY.register(new LootItem(LootCategory.THROWABLES, Material.EGG, "Egg of Doom &8- &7Hunger and Confusion"));
    public static final LootItem SLOWBALL = REGISTRY.register(new LootItem(LootCategory.THROWABLES, Material.SNOW_BALL, "Slowball &8- &7Slowness"));
    public static final LootItem TNT = REGISTRY.register(new LootItem(LootCategory.PLACEABLES, Material.TNT, "TNT"));
    public static final LootItem COBWEB = REGISTRY.register(new LootItem(LootCategory.PLACEABLES, Material.WEB, "Cobweb"));
    public static final LootItem XP_BOTTLE = REGISTRY.register(new LootItem(LootCategory.THROWABLES, Material.EXP_BOTTLE, "XP Bottle"));
    public static final LootItem WET_NODDLE = REGISTRY.register(new LootItem(LootCategory.MISC_FOOD, Material.ROTTEN_FLESH, "Wet Noodle", Collections.singletonList("&7&oEat this to get a 15% chance to get Strength II")));
    public static final LootItem ENDER_PEARL = REGISTRY.register(new LootItem(LootCategory.THROWABLES, Material.ENDER_PEARL));
    
    public static final LootItem PLAYER_TRACKER = REGISTRY.register(new LootItem(LootCategory.TOOLS, Material.COMPASS, "Player Tracker"));
    public static final LootItem FLINT_AND_STEEL = REGISTRY.register(new LootItem(LootCategory.TOOLS, Material.FLINT_AND_STEEL));
    public static final LootItem FISHING_ROD = REGISTRY.register(new LootItem(LootCategory.TOOLS, Material.FISHING_ROD));
    
    public static final LootItem IRON_INGOT = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.IRON_INGOT));
    public static final LootItem GOLD_INGOT = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.GOLD_INGOT));
    public static final LootItem STICK = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.STICK));
    public static final LootItem FEATHER = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.FEATHER));
    public static final LootItem FLINT = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.FLINT));
    public static final LootItem DIAMOND = REGISTRY.register(new LootItem(LootCategory.COMPONENTS, Material.DIAMOND));
}