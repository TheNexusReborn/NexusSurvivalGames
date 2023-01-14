package com.thenexusreborn.survivalgames.sponsoring;

import com.thenexusreborn.survivalgames.loot.*;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SponsorManager {
    
    private SponsorCategory<LootItem> foodCategory, weaponCategory, armorCategory;
    private SponsorCategory<PotionEffectType> potionCategory;
    
    private List<SponsorCategory<?>> categories = new LinkedList<>();
    
    public SponsorManager() {
        this.foodCategory = new ItemSponsorCategory("Food", Material.COOKED_BEEF);
        this.foodCategory.addEntries(Items.PORKCHOP, Items.RAW_PORKCHOP, Items.RAW_BEEF, Items.STEAK, Items.GRILLED_CHICKEN, Items.RAW_CHICKEN, Items.CARROT, Items.POTATO, Items.RAW_FISH, Items.VILE_CREATURE, Items.APPLE, Items.MELON, Items.COOKIE);
        this.categories.add(foodCategory);
        
        this.weaponCategory = new ItemSponsorCategory("Weapon", Material.WOOD_SWORD);
        this.weaponCategory.addEntries(Items.WOOD_AXE, Items.WOOD_SWORD, Items.BOW, Items.ARROW, Items.STONE_SWORD);
        this.categories.add(weaponCategory);
        
        this.armorCategory = new ItemSponsorCategory("Armor", Material.LEATHER_CHESTPLATE);
        this.armorCategory.addEntries(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.THE_CROWN, Items.GOLD_CHESTPLATE, Items.GOLD_LEGGINGS, Items.GOLD_BOOTS, Items.LINGERIE_HELMET, Items.LINGERIE_CHESTPLATE, Items.LINGERIE_LEGGINGS, Items.LINGERIE_BOOTS);
        this.categories.add(armorCategory);
        
        this.potionCategory = new PotionSponsorCategory("Potion Effect", Material.POTION);
        this.potionCategory.addEntries(PotionEffectType.SPEED, PotionEffectType.SLOW, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.CONFUSION, PotionEffectType.JUMP, PotionEffectType.REGENERATION, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS, PotionEffectType.POISON);
        this.categories.add(potionCategory);
    }
    
    public List<SponsorCategory<?>> getCategories() {
        return categories;
    }
    
    public SponsorCategory<LootItem> getFoodCategory() {
        return foodCategory;
    }
    
    public SponsorCategory<LootItem> getWeaponCategory() {
        return weaponCategory;
    }
    
    public SponsorCategory<LootItem> getArmorCategory() {
        return armorCategory;
    }
    
    public SponsorCategory<PotionEffectType> getPotionCategory() {
        return potionCategory;
    }
}
