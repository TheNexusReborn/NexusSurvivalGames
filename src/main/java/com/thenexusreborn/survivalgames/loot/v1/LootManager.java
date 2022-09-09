package com.thenexusreborn.survivalgames.loot.v1;

import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.Material;

import java.util.*;

public class LootManager {
    private final Map<Material, Loot> possibleLoot = new HashMap<>();
    private final List<Loot> lootChances = new ArrayList<>();
    
    private final SurvivalGames plugin;
    
    public LootManager(SurvivalGames plugin) {
        this.plugin = plugin;
        Loot leather_helmet = new Loot(Material.LEATHER_HELMET, 25);
        Loot leather_chestplate = new Loot(Material.LEATHER_CHESTPLATE, 25);
        Loot leather_leggings = new Loot(Material.LEATHER_LEGGINGS, 25);
        Loot leather_boots = new Loot(Material.LEATHER_BOOTS, 25);
        Loot chain_helmet = new Loot(Material.CHAINMAIL_HELMET, "Lingerie Helmet", 20);
        Loot chain_chestplate = new Loot(Material.CHAINMAIL_CHESTPLATE, "Lingerie Chestplate", 20);
        Loot chain_leggings = new Loot(Material.CHAINMAIL_LEGGINGS, "Lingerie Leggings", 20);
        Loot chain_boots = new Loot(Material.CHAINMAIL_BOOTS, "Lingerie Boots", 20);
        Loot iron_helmet = new Loot(Material.IRON_HELMET, 5);
        Loot iron_chestplate = new Loot(Material.IRON_CHESTPLATE, 5);
        Loot iron_leggings = new Loot(Material.IRON_LEGGINGS, 5);
        Loot iron_boots = new Loot(Material.IRON_BOOTS, 5);
        Loot gold_helmet = new Loot(Material.GOLD_HELMET, 20);
        Loot gold_chestplate = new Loot(Material.GOLD_CHESTPLATE, 20);
        Loot gold_leggings = new Loot(Material.GOLD_LEGGINGS, 20);
        Loot gold_boots = new Loot(Material.GOLD_BOOTS, 20);
        Loot wood_axe = new Loot(Material.WOOD_AXE, 40);
        Loot wood_sword = new Loot(Material.WOOD_SWORD, 40);
        Loot stone_axe = new Loot(Material.STONE_AXE, 30);
        Loot stone_sword = new Loot(Material.STONE_SWORD, 30);
        Loot bow = new Loot(Material.BOW, 20);
        Loot arrows = new Loot(Material.ARROW, 30, 5);
        Loot cooked_porkchop = new Loot(Material.GRILLED_PORK, "Porkchop", 30);
        Loot steak = new Loot(Material.COOKED_BEEF, "Steak", 30);
        Loot grilled_chicken = new Loot(Material.COOKED_CHICKEN, "Grilled Chicken", 30);
        Loot raw_porkchop = new Loot(Material.PORK, "Raw Porkchop", 30);
        Loot raw_beef = new Loot(Material.RAW_BEEF, 50);
        Loot raw_chicken = new Loot(Material.RAW_CHICKEN, 50);
        Loot carrots = new Loot(Material.CARROT_ITEM, "Carrot", 40);
        Loot potato = new Loot(Material.POTATO_ITEM, "Potato", 40);
        Loot baked_potato = new Loot(Material.BAKED_POTATO, 20);
        Loot egg_of_doom = new Loot(Material.EGG, "Egg of Doom", 15);
        Loot slowball = new Loot(Material.SNOW_BALL, "Slowball", 20);
        Loot iron_ingot = new Loot(Material.IRON_INGOT, 7);
        Loot gold_ingot = new Loot(Material.GOLD_INGOT, 15);
        Loot stick = new Loot(Material.STICK, 20);
        Loot flint_and_steel = new Loot(Material.FLINT_AND_STEEL, 15);
        Loot tnt = new Loot(Material.TNT, "TNT", 8);
        Loot iron_axe = new Loot(Material.IRON_AXE, 7);
        Loot cake = new Loot(Material.CAKE, 20);
        Loot player_tracker = new Loot(Material.COMPASS, "Player Tracker", 20);
        Loot pumpkin_pie = new Loot(Material.PUMPKIN_PIE, 25);
        Loot raw_fish = new Loot(Material.RAW_FISH, 50);
        Loot cooked_fish = new Loot(Material.COOKED_FISH, "Vile Creature", 35);
        Loot feather = new Loot(Material.FEATHER, 25);
        Loot flint = new Loot(Material.FLINT, 15);
        Loot fishing_rod = new Loot(Material.FISHING_ROD, 15);
        Loot cobweb = new Loot(Material.WEB, "Cobweb", 15);
        Loot enchantment_bottle = new Loot(Material.EXP_BOTTLE, 25, 3);
        Loot golden_apple = new Loot(Material.GOLDEN_APPLE, "Golden Munchie", 2);
        //Loot wood_planks = new Loot(Material.WOOD, "Wood Planks", 15);
        Loot ender_pearl = new Loot(Material.ENDER_PEARL, 1);
        Loot wet_noodle = new Loot(Material.ROTTEN_FLESH, "Wet Noodle", 5);
        Loot golden_carrot = new Loot(Material.GOLDEN_CARROT, 20);
        Loot diamond = new Loot(Material.DIAMOND, 7);
        Loot apple = new Loot(Material.APPLE, 20);
        
        addLoot(leather_helmet, leather_chestplate, leather_leggings, leather_boots, chain_helmet, chain_chestplate, chain_leggings, chain_boots, iron_helmet, iron_chestplate,
                iron_leggings, iron_boots, gold_helmet, gold_chestplate, gold_leggings, gold_boots, wood_axe, wood_sword, stone_axe, stone_sword, bow, arrows,
                cooked_porkchop, steak, grilled_chicken, raw_porkchop, raw_beef, raw_chicken, carrots, potato, baked_potato, iron_ingot, gold_ingot, stick, flint_and_steel, tnt,
                iron_axe, cake, pumpkin_pie, raw_fish, cooked_fish, feather, flint, fishing_rod, cobweb, enchantment_bottle, golden_apple, wet_noodle, ender_pearl, golden_carrot, diamond, egg_of_doom, slowball, player_tracker, apple);
        
        for (Loot loot : this.possibleLoot.values()) {
            for (int i = 0; i < loot.getWeight(); i++) {
                this.lootChances.add(loot);
            }
        }
        
        Collections.shuffle(this.lootChances);
    }
    
    public List<Loot> generateLoot(int amount) {
        List<Loot> loot = new ArrayList<>();
        
        for (int i = 0; i < amount; i++) {
            Loot l = lootChances.get(new Random().nextInt(lootChances.size()));
            loot.add(l);
        }
        
        return loot;
    }
    
    public void addLoot(Loot... loots) {
        for (Loot loot : loots) {
            addPossibleLoot(loot);
        }
    }
    
    public void addPossibleLoot(Loot loot) {
        this.possibleLoot.put(loot.getMaterial(), loot);
    }
}
