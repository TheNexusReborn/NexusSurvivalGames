package com.thenexusreborn.survivalgames.loot.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import static com.thenexusreborn.survivalgames.loot.category.LootCategory.*;

public final class Items {
    public static final ItemRegistry REGISTRY = new ItemRegistry();
    public static final LootRegisterer REGISTERER = new LootRegisterer(REGISTRY);
    
    public static final LootItem PORKCHOP = REGISTERER.register("Porkchop", Material.GRILLED_PORK, FOOD, COOKED_FOOD).get();
    public static final LootItem STEAK = REGISTERER.register("Steak", Material.COOKED_BEEF, FOOD, COOKED_FOOD).get();
    public static final LootItem GRILLED_CHICKEN = REGISTERER.register("Grilled Chicken", Material.COOKED_CHICKEN, FOOD, COOKED_FOOD).get();
    public static final LootItem RAW_PORKCHOP = REGISTERER.register("Raw Porkchop", Material.PORK, FOOD, COOKED_FOOD).get();
    public static final LootItem RAW_BEEF = REGISTERER.register(Material.RAW_BEEF, FOOD, RAW_FOOD).get();
    public static final LootItem RAW_CHICKEN = REGISTERER.register(Material.RAW_CHICKEN, FOOD, RAW_FOOD).get();
    public static final LootItem CARROT = REGISTERER.register("Carrot", Material.CARROT_ITEM, FOOD, MISC_FOOD).get();
    public static final LootItem POTATO = REGISTERER.register(Material.POTATO_ITEM, FOOD, MISC_FOOD).get();
    public static final LootItem BAKED_POTATO = REGISTERER.register(Material.BAKED_POTATO, FOOD, COOKED_FOOD).get();
    public static final LootItem CAKE = REGISTERER.register(Material.CAKE, FOOD, MISC_FOOD).get();
    public static final LootItem PUMPKIN_PIE = REGISTERER.register(Material.PUMPKIN_PIE, FOOD, MISC_FOOD).get();
    public static final LootItem RAW_FISH = REGISTERER.register(Material.RAW_FISH, FOOD, RAW_FOOD).get();
    public static final LootItem VILE_CREATURE = REGISTERER.register("Vile Creature", Material.COOKED_FISH, FOOD, COOKED_FOOD).get();
    public static final LootItem GOLDEN_CARROT = REGISTERER.register(Material.GOLDEN_CARROT, FOOD, MISC_FOOD).get();
    public static final LootItem APPLE = REGISTERER.register(Material.APPLE, FOOD, MISC_FOOD).get();
    public static final LootItem MELON = REGISTERER.register("turqMelon", Material.MELON, FOOD, MISC_FOOD).get();
    public static final LootItem GOLDEN_MUNCHIE = REGISTERER.register("Golden Munchie", Material.GOLDEN_APPLE, FOOD, COOKED_FOOD).get();
    public static final LootItem COOKIE = REGISTERER.register(Material.COOKIE, FOOD, MISC_FOOD).get();
    public static final LootItem MUSHROOM_SOUP = REGISTERER.register(Material.MUSHROOM_SOUP, FOOD, COOKED_FOOD).get();
    
    public static final LootItem LEATHER_HELMET = REGISTERER.register(Material.LEATHER_HELMET, ARMOR, LEATHER_ARMOR, HELMETS).get();
    public static final LootItem LEATHER_CHESTPLATE = REGISTERER.register(Material.LEATHER_CHESTPLATE, ARMOR, LEATHER_ARMOR, CHESTPLATES).get();
    public static final LootItem LEATHER_LEGGINGS = REGISTERER.register(Material.LEATHER_LEGGINGS, ARMOR, LEATHER_ARMOR, LEGGINGS).get();
    public static final LootItem LEATHER_BOOTS = REGISTERER.register(Material.LEATHER_BOOTS, ARMOR, LEATHER_ARMOR, BOOTS).get();
    public static final LootItem THE_CROWN = REGISTERER.register("&6The Crown", Material.GOLD_HELMET, ARMOR, GOLD_ARMOR, HELMETS).get();
    public static final LootItem GOLD_CHESTPLATE = REGISTERER.register(Material.GOLD_CHESTPLATE, ARMOR, GOLD_ARMOR, CHESTPLATES).get();
    public static final LootItem GOLD_LEGGINGS = REGISTERER.register(Material.GOLD_LEGGINGS, ARMOR, GOLD_ARMOR, LEGGINGS).get();
    public static final LootItem GOLD_BOOTS = REGISTERER.register(Material.GOLD_BOOTS, ARMOR, GOLD_ARMOR, BOOTS).get();
    public static final LootItem LINGERIE_HELMET = REGISTERER.register("Lingerie Helmet", Material.CHAINMAIL_HELMET, ARMOR, CHAINMAIL_ARMOR, HELMETS).get();
    public static final LootItem LINGERIE_CHESTPLATE = REGISTERER.register("Lingerie Chestplate", Material.CHAINMAIL_CHESTPLATE, ARMOR, CHAINMAIL_ARMOR, CHESTPLATES).get();
    public static final LootItem LINGERIE_LEGGINGS = REGISTERER.register("Lingerie Leggings", Material.CHAINMAIL_LEGGINGS, ARMOR, CHAINMAIL_ARMOR, LEGGINGS).get();
    public static final LootItem LINGERIE_BOOTS = REGISTERER.register("Lingerie Boots", Material.CHAINMAIL_BOOTS, ARMOR, CHAINMAIL_ARMOR, BOOTS).get();
    public static final LootItem IRON_HELMET = REGISTERER.register(Material.IRON_HELMET, ARMOR, IRON_ARMOR, HELMETS).get();
    public static final LootItem IRON_CHESTPLATE = REGISTERER.register(Material.IRON_CHESTPLATE, ARMOR, IRON_ARMOR, CHESTPLATES).get();
    public static final LootItem IRON_LEGGINGS = REGISTERER.register(Material.IRON_LEGGINGS, ARMOR, IRON_ARMOR, LEGGINGS).get();
    public static final LootItem IRON_BOOTS = REGISTERER.register(Material.IRON_BOOTS, ARMOR, IRON_ARMOR, BOOTS).get();
    public static final LootItem DIAMOND_HELMET = REGISTERER.register(Material.DIAMOND_HELMET, ARMOR, DIAMOND_ARMOR, HELMETS).get();
    public static final LootItem DIAMOND_CHESTPLATE = REGISTERER.register(Material.DIAMOND_CHESTPLATE, ARMOR, DIAMOND_ARMOR, CHESTPLATES).get();
    public static final LootItem DIAMOND_LEGGINGS = REGISTERER.register(Material.DIAMOND_LEGGINGS, ARMOR, DIAMOND_ARMOR, LEGGINGS).get();
    public static final LootItem DIAMOND_BOOTS = REGISTERER.register(Material.DIAMOND_BOOTS, ARMOR, DIAMOND_ARMOR, BOOTS).get();
    
    public static final LootItem WOOD_AXE = REGISTERER.register(Material.WOOD_AXE, WEAPONS, WOODEN_WEAPONS, AXES).get();
    public static final LootItem WOOD_SWORD = REGISTERER.register(Material.WOOD_SWORD, WEAPONS, WOODEN_WEAPONS, SWORDS).get();
    public static final LootItem SACRIFICIAL_AXE = REGISTERER.register("&4&l&oSacrificial Axe", Material.GOLD_AXE, WEAPONS, GOLD_WEAPONS, AXES).get().setLore("", "&7In honor of TheDragonFox").addEnchantment(Enchantment.DURABILITY, 1);
    public static final LootItem SACRIFICIAL_SWORD = REGISTERER.register("&4&l&oSacrificial Sword", Material.GOLD_SWORD, WEAPONS, GOLD_WEAPONS, SWORDS).get().setLore("", "&7In honor of TheDragonFox").addEnchantment(Enchantment.DURABILITY, 1);
    public static final LootItem STONE_AXE = REGISTERER.register(Material.STONE_AXE, WEAPONS, STONE_WEAPONS, AXES).get();
    public static final LootItem STONE_SWORD = REGISTERER.register(Material.STONE_SWORD, WEAPONS, STONE_WEAPONS, SWORDS).get();
    public static final LootItem FREDERICK = REGISTERER.register("Frederick", Material.IRON_AXE, WEAPONS, IRON_WEAPONS, AXES).get();
    public static final LootItem IRON_SWORD = REGISTERER.register(Material.IRON_SWORD, WEAPONS, IRON_WEAPONS, SWORDS).get();
    public static final LootItem BETTY = REGISTERER.register("&bBetty", Material.DIAMOND_AXE, WEAPONS, DIAMOND_WEAPONS, AXES).get();
    public static final LootItem DIAMOND_SWORD = REGISTERER.register(Material.DIAMOND_SWORD, WEAPONS, DIAMOND_WEAPONS, SWORDS).get();
    public static final LootItem BOW = REGISTERER.register(Material.BOW, WEAPONS, RANGED).get();
    public static final LootItem ARROW = REGISTERER.register(Material.ARROW, WEAPONS, RANGED).get().setAmount(3);
    
    public static final LootItem EGG_OF_DOOM = REGISTERER.register("Egg of Doom &8- &7Hunger and Confusion", Material.EGG, THROWABLES).get();
    public static final LootItem SLOWBALL = REGISTERER.register("Slowball &8- &7Slowness", Material.SNOW_BALL, THROWABLES).get();
    public static final LootItem TNT = REGISTERER.register("TNT", Material.TNT, PLACEABLES).get();
    public static final LootItem COBWEB = REGISTERER.register("Cobweb", Material.WEB, PLACEABLES).get();
    public static final LootItem XP_BOTTLE = REGISTERER.register("XP Bottle", Material.EXP_BOTTLE, THROWABLES).get().setAmount(3);
    public static final LootItem WET_NODDLE = REGISTERER.register("Wet Noodle", Material.ROTTEN_FLESH, FOOD, MISC_FOOD).get().setLore("&7&oEat this to get a 15% chance to get Strength II");
    public static final LootItem ENDER_PEARL = REGISTERER.register(Material.ENDER_PEARL, THROWABLES).get();
    
    public static final LootItem PLAYER_TRACKER = REGISTERER.register("Player Tracker", Material.COMPASS, TOOLS).get();
    public static final LootItem FLINT_AND_STEEL = REGISTERER.register(Material.FLINT_AND_STEEL, TOOLS).get();
    public static final LootItem FISHING_ROD = REGISTERER.register(Material.FISHING_ROD, TOOLS).get();
    
    public static final LootItem IRON_INGOT = REGISTERER.register(Material.IRON_INGOT, COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT, ARMOR_COMPONENT).get();
    public static final LootItem GOLD_INGOT = REGISTERER.register(Material.GOLD_INGOT, COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT).get();
    public static final LootItem STICK = REGISTERER.register(Material.STICK, COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT).get();
    public static final LootItem FEATHER = REGISTERER.register(Material.FEATHER, COMPONENTS, WEAPON_COMPONENT).get();
    public static final LootItem FLINT = REGISTERER.register(Material.FLINT, COMPONENTS, TOOL_COMPONENT).get();
    public static final LootItem DIAMOND = REGISTERER.register(Material.DIAMOND, COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT).get();
    
    static {
        Recipe sacrificialAxeRecipe = new ShapelessRecipe(SACRIFICIAL_AXE.getItemStack())
                .addIngredient(7, Material.GOLD_INGOT)
                .addIngredient(1, Material.DIAMOND)
                .addIngredient(Material.STONE_AXE);
        Bukkit.getServer().addRecipe(sacrificialAxeRecipe);
        
        Recipe sacrificialSwordRecipe = new ShapelessRecipe(SACRIFICIAL_SWORD.getItemStack())
                .addIngredient(7, Material.GOLD_INGOT)
                .addIngredient(1, Material.DIAMOND)
                .addIngredient(Material.STONE_SWORD);
        Bukkit.getServer().addRecipe(sacrificialSwordRecipe);
    }
}