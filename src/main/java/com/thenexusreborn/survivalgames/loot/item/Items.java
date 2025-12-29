package com.thenexusreborn.survivalgames.loot.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;

import static com.thenexusreborn.survivalgames.loot.category.LootCategory.*;

public final class Items {
    public static final ItemRegistry REGISTRY = new ItemRegistry();
    
    public static final LootItem PORKCHOP = REGISTRY.register("Porkchop", Material.GRILLED_PORK, FOOD, COOKED_FOOD).get();
    public static final LootItem STEAK = REGISTRY.register("Steak", Material.COOKED_BEEF, FOOD, COOKED_FOOD).get();
    public static final LootItem GRILLED_CHICKEN = REGISTRY.register("Grilled Chicken", Material.COOKED_CHICKEN, FOOD, COOKED_FOOD).get();
    public static final LootItem RAW_PORKCHOP = REGISTRY.register("Raw Porkchop", Material.PORK, FOOD, COOKED_FOOD).get();
    public static final LootItem RAW_BEEF = REGISTRY.register(Material.RAW_BEEF, FOOD, RAW_FOOD).get();
    public static final LootItem RAW_CHICKEN = REGISTRY.register(Material.RAW_CHICKEN, FOOD, RAW_FOOD).get();
    public static final LootItem CARROT = REGISTRY.register("Carrot", Material.CARROT_ITEM, FOOD, MISC_FOOD).get();
    public static final LootItem POTATO = REGISTRY.register(Material.POTATO_ITEM, FOOD, MISC_FOOD).get();
    public static final LootItem BAKED_POTATO = REGISTRY.register(Material.BAKED_POTATO, FOOD, COOKED_FOOD).get();
    public static final LootItem CAKE = REGISTRY.register(Material.CAKE, FOOD, MISC_FOOD).get();
    public static final LootItem PUMPKIN_PIE = REGISTRY.register(Material.PUMPKIN_PIE, FOOD, MISC_FOOD).get();
    public static final LootItem RAW_FISH = REGISTRY.register(Material.RAW_FISH, FOOD, RAW_FOOD).get();
    public static final LootItem VILE_CREATURE = REGISTRY.register("Vile Creature", Material.COOKED_FISH, FOOD, COOKED_FOOD).get();
    public static final LootItem GOLDEN_CARROT = REGISTRY.register(Material.GOLDEN_CARROT, FOOD, MISC_FOOD).get();
    public static final LootItem APPLE = REGISTRY.register(Material.APPLE, FOOD, MISC_FOOD).get();
    public static final LootItem MELON = REGISTRY.register("turqMelon", Material.MELON, FOOD, MISC_FOOD).get();
    public static final LootItem GOLDEN_MUNCHIE = REGISTRY.register("Golden Munchie", Material.GOLDEN_APPLE, FOOD, COOKED_FOOD).get();
    public static final LootItem COOKIE = REGISTRY.register(Material.COOKIE, FOOD, MISC_FOOD).get();
    public static final LootItem MUSHROOM_SOUP = REGISTRY.register(Material.MUSHROOM_SOUP, FOOD, COOKED_FOOD).get();
    
    public static final LootItem LEATHER_HELMET = REGISTRY.register(Material.LEATHER_HELMET, ARMOR, LEATHER_ARMOR, HELMETS).get();
    public static final LootItem LEATHER_CHESTPLATE = REGISTRY.register(Material.LEATHER_CHESTPLATE, ARMOR, LEATHER_ARMOR, CHESTPLATES).get();
    public static final LootItem LEATHER_LEGGINGS = REGISTRY.register(Material.LEATHER_LEGGINGS, ARMOR, LEATHER_ARMOR, LEGGINGS).get();
    public static final LootItem LEATHER_BOOTS = REGISTRY.register(Material.LEATHER_BOOTS, ARMOR, LEATHER_ARMOR, BOOTS).get();
    public static final LootItem THE_CROWN = REGISTRY.register("&6The Crown", Material.GOLD_HELMET, ARMOR, GOLD_ARMOR, HELMETS).get();
    public static final LootItem GOLD_CHESTPLATE = REGISTRY.register(Material.GOLD_CHESTPLATE, ARMOR, GOLD_ARMOR, CHESTPLATES).get();
    public static final LootItem GOLD_LEGGINGS = REGISTRY.register(Material.GOLD_LEGGINGS, ARMOR, GOLD_ARMOR, LEGGINGS).get();
    public static final LootItem GOLD_BOOTS = REGISTRY.register(Material.GOLD_BOOTS, ARMOR, GOLD_ARMOR, BOOTS).get();
    public static final LootItem LINGERIE_HELMET = REGISTRY.register("Lingerie Helmet", Material.CHAINMAIL_HELMET, ARMOR, CHAINMAIL_ARMOR, HELMETS).get();
    public static final LootItem LINGERIE_CHESTPLATE = REGISTRY.register("Lingerie Chestplate", Material.CHAINMAIL_CHESTPLATE, ARMOR, CHAINMAIL_ARMOR, CHESTPLATES).get();
    public static final LootItem LINGERIE_LEGGINGS = REGISTRY.register("Lingerie Leggings", Material.CHAINMAIL_LEGGINGS, ARMOR, CHAINMAIL_ARMOR, LEGGINGS).get();
    public static final LootItem LINGERIE_BOOTS = REGISTRY.register("Lingerie Boots", Material.CHAINMAIL_BOOTS, ARMOR, CHAINMAIL_ARMOR, BOOTS).get();
    public static final LootItem IRON_HELMET = REGISTRY.register(Material.IRON_HELMET, ARMOR, IRON_ARMOR, HELMETS).get();
    public static final LootItem IRON_CHESTPLATE = REGISTRY.register(Material.IRON_CHESTPLATE, ARMOR, IRON_ARMOR, CHESTPLATES).get();
    public static final LootItem IRON_LEGGINGS = REGISTRY.register(Material.IRON_LEGGINGS, ARMOR, IRON_ARMOR, LEGGINGS).get();
    public static final LootItem IRON_BOOTS = REGISTRY.register(Material.IRON_BOOTS, ARMOR, IRON_ARMOR, BOOTS).get();
    public static final LootItem DIAMOND_HELMET = REGISTRY.register(Material.DIAMOND_HELMET, ARMOR, DIAMOND_ARMOR, HELMETS).get();
    public static final LootItem DIAMOND_CHESTPLATE = REGISTRY.register(Material.DIAMOND_CHESTPLATE, ARMOR, DIAMOND_ARMOR, CHESTPLATES).get();
    public static final LootItem DIAMOND_LEGGINGS = REGISTRY.register(Material.DIAMOND_LEGGINGS, ARMOR, DIAMOND_ARMOR, LEGGINGS).get();
    public static final LootItem DIAMOND_BOOTS = REGISTRY.register(Material.DIAMOND_BOOTS, ARMOR, DIAMOND_ARMOR, BOOTS).get();
    
    public static final LootItem WOOD_AXE = REGISTRY.register(Material.WOOD_AXE, WEAPONS, WOODEN_WEAPONS, AXES).get();
    public static final LootItem WOOD_SWORD = REGISTRY.register(Material.WOOD_SWORD, WEAPONS, WOODEN_WEAPONS, SWORDS).get();
    public static final LootItem SACRIFICIAL_AXE = REGISTRY.register("&4&l&oSacrificial Axe", Material.GOLD_AXE, WEAPONS, GOLD_WEAPONS, AXES).get().setLore("", "&7In honor of TheDragonFox").addEnchantment(Enchantment.DURABILITY, 1);
    public static final LootItem SACRIFICIAL_SWORD = REGISTRY.register("&4&l&oSacrificial Sword", Material.GOLD_SWORD, WEAPONS, GOLD_WEAPONS, SWORDS).get().setLore("", "&7In honor of TheDragonFox").addEnchantment(Enchantment.DURABILITY, 1);
    public static final LootItem STONE_AXE = REGISTRY.register(Material.STONE_AXE, WEAPONS, STONE_WEAPONS, AXES).get();
    public static final LootItem STONE_SWORD = REGISTRY.register(Material.STONE_SWORD, WEAPONS, STONE_WEAPONS, SWORDS).get();
    public static final LootItem FREDERICK = REGISTRY.register("Frederick", Material.IRON_AXE, WEAPONS, IRON_WEAPONS, AXES).get();
    public static final LootItem IRON_SWORD = REGISTRY.register(Material.IRON_SWORD, WEAPONS, IRON_WEAPONS, SWORDS).get();
    public static final LootItem BETTY = REGISTRY.register("&bBetty", Material.DIAMOND_AXE, WEAPONS, DIAMOND_WEAPONS, AXES).get();
    public static final LootItem DIAMOND_SWORD = REGISTRY.register(Material.DIAMOND_SWORD, WEAPONS, DIAMOND_WEAPONS, SWORDS).get();
    public static final LootItem BOW = REGISTRY.register(Material.BOW, WEAPONS, RANGED).get();
    public static final LootItem ARROW = REGISTRY.register(Material.ARROW, WEAPONS, RANGED).get().setAmount(3);
    
    public static final LootItem EGG_OF_DOOM = REGISTRY.register("Egg of Doom &8- &7Hunger and Confusion", Material.EGG, THROWABLES).get();
    public static final LootItem SLOWBALL = REGISTRY.register("Slowball &8- &7Slowness", Material.SNOW_BALL, THROWABLES).get();
    public static final LootItem TNT = REGISTRY.register("TNT", Material.TNT, PLACEABLES).get();
    public static final LootItem COBWEB = REGISTRY.register("Cobweb", Material.WEB, PLACEABLES).get();
    public static final LootItem XP_BOTTLE = REGISTRY.register("XP Bottle", Material.EXP_BOTTLE, THROWABLES).get().setAmount(3);
    public static final LootItem WET_NODDLE = REGISTRY.register("Wet Noodle", Material.ROTTEN_FLESH, FOOD, MISC_FOOD).get().setLore("&7&oEat this to get a 15% chance to get Strength II");
    public static final LootItem ENDER_PEARL = REGISTRY.register(Material.ENDER_PEARL, THROWABLES).get();
    
    public static final LootItem PLAYER_TRACKER = REGISTRY.register("Player Tracker", Material.COMPASS, TOOLS).get();
    public static final LootItem FLINT_AND_STEEL = REGISTRY.register(Material.FLINT_AND_STEEL, TOOLS).get();
    public static final LootItem FISHING_ROD = REGISTRY.register(Material.FISHING_ROD, TOOLS).get();
    
    public static final LootItem IRON_INGOT = REGISTRY.register(Material.IRON_INGOT, COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT, ARMOR_COMPONENT).get();
    public static final LootItem GOLD_INGOT = REGISTRY.register(Material.GOLD_INGOT, COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT).get();
    public static final LootItem STICK = REGISTRY.register(Material.STICK, COMPONENTS, WEAPON_COMPONENT, TOOL_COMPONENT).get();
    public static final LootItem FEATHER = REGISTRY.register(Material.FEATHER, COMPONENTS, WEAPON_COMPONENT).get();
    public static final LootItem FLINT = REGISTRY.register(Material.FLINT, COMPONENTS, TOOL_COMPONENT).get();
    public static final LootItem DIAMOND = REGISTRY.register(Material.DIAMOND, COMPONENTS, WEAPON_COMPONENT, ARMOR_COMPONENT).get();
    
    static {
        Recipe sacrificialAxeRecipe = new ShapelessRecipe(SACRIFICIAL_AXE.getItemStack())
                .addIngredient(8, Material.GOLD_INGOT)
                .addIngredient(Material.DIAMOND_AXE);
        Bukkit.getServer().addRecipe(sacrificialAxeRecipe);
        
        Recipe sacrificialSwordRecipe = new ShapelessRecipe(SACRIFICIAL_SWORD.getItemStack())
                .addIngredient(8, Material.GOLD_INGOT)
                .addIngredient(Material.DIAMOND_SWORD);
        Bukkit.getServer().addRecipe(sacrificialSwordRecipe);
    }
}