package com.thenexusreborn.survivalgames.mutations;

import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.disguise.disguisetypes.DisguiseType;
import com.thenexusreborn.nexuscore.util.ArmorType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;
import static org.bukkit.potion.PotionEffectType.*;

public class MutationType {
    private static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilder.start(Material.GOLD_SWORD).unbreakable(true);
    
    public static final MutationType PIG_ZOMBIE = new Builder("pig_zombie", ArmorType.LEATHER, UNBREAKABLE_GOLD_SWORD.build(), DisguiseType.PIG_ZOMBIE).addDamageImmunity(FIRE).addDamageImmunity(FIRE_TICK).addEffect(SPEED, 1).healthRegen(false).create();
    public static final MutationType ZOMBIE = new Builder("zombie", ArmorType.IRON, UNBREAKABLE_GOLD_SWORD.build(), DisguiseType.ZOMBIE).unlockCost(3000).addEffect(SLOW, 0).create();
    public static final MutationType ENDERMAN = new Builder("enderman", ArmorType.LEATHER, UNBREAKABLE_GOLD_SWORD.build(), DisguiseType.ENDERMAN).unlockCost(5000).addItem(1, new ItemStack(Material.ENDER_PEARL, 32)).create();
    public static final MutationType SKELETON = new Builder("skeleton", ArmorType.LEATHER, new ItemStack(Material.WOOD_SWORD), DisguiseType.SKELETON).unlockCost(3000).addItem(1, ItemBuilder.start(Material.BOW).addEnchantment(Enchantment.ARROW_INFINITE, 1).unbreakable(true).build()).addItem(2, new ItemStack(Material.ARROW)).addEffect(SPEED, 0).create();
    public static final MutationType CHICKEN = new Builder("chicken", ArmorType.NONE, ItemBuilder.start(Material.STONE_SWORD).displayName("&bEgg Launcher").addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.ARROW_DAMAGE, 1).build(), DisguiseType.CHICKEN).health(4).unlockCost(5000).addItem(1, ItemBuilder.start(Material.SLIME_BALL).displayName("&bChicken Launch").build()).addItem(2, ItemBuilder.start(Material.FEATHER).displayName("&bChicken Parachute").build()).create();
    public static final MutationType CREEPER = new Builder("creeper", ArmorType.CHAINMAIL, new ItemStack(Material.TNT, 64), DisguiseType.CREEPER).health(10).unlockCost(3000).addDamageImmunity(BLOCK_EXPLOSION).addDamageImmunity(ENTITY_EXPLOSION).addEffect(SPEED, 0).addItem(1, ItemBuilder.start(Material.SULPHUR).displayName("&cSuicide").build()).create();
    
//    TODO (These are MVP only) Blaze, Spider, Witch and Wither Skeleton
    
    private String id;
    private String displayName;
    private ArmorType armorType;
    private ItemStack weapon;
    private DisguiseType disguiseType;
    private int unlockCost;
    private int perUseCost;
    private int health;
    private boolean healthRegen;
    private List<DamageCause> damageImmunities;
    private List<MutationEffect> effects;
    private List<MutationItem> items;
    
    MutationType(String id, String displayName, ArmorType armorType, ItemStack weapon, DisguiseType disguiseType, int unlockCost, int perUseCost, int health, boolean healthRegen, List<DamageCause> damageImmunities, List<MutationEffect> effects, List<MutationItem> items) {
        this.id = id;
        this.displayName = displayName;
        this.armorType = armorType;
        this.weapon = weapon;
        this.disguiseType = disguiseType;
        this.unlockCost = unlockCost;
        this.perUseCost = perUseCost;
        this.health = health;
        this.healthRegen = healthRegen;
        this.damageImmunities = damageImmunities;
        this.effects = effects;
        this.items = items;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ArmorType getArmorType() {
        return armorType;
    }
    
    public ItemStack getWeapon() {
        return weapon;
    }
    
    public DisguiseType getDisguiseType() {
        return disguiseType;
    }
    
    public int getUnlockCost() {
        return unlockCost;
    }
    
    public int getPerUseCost() {
        return perUseCost;
    }
    
    public int getHealth() {
        return health;
    }
    
    public List<DamageCause> getDamageImmunities() {
        return damageImmunities;
    }
    
    public List<MutationEffect> getEffects() {
        return effects;
    }
    
    public List<MutationItem> getItems() {
        return items;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutationType that = (MutationType) o;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    private static class Builder {
        private String id;
        private String displayName;
        private ArmorType armorType;
        private ItemStack weapon;
        private DisguiseType disguiseType;
        private int unlockCost;
        private int perUseCost;
        private int health;
        private boolean healthRegen = true;
        private List<DamageCause> damageImmunities = new ArrayList<>();
        private List<MutationEffect> effects = new ArrayList<>();
        private List<MutationItem> items = new ArrayList<>();
        
        public Builder(String id, ArmorType armorType, ItemStack weapon, DisguiseType disguiseType) {
            this.id = id;
            this.armorType = armorType;
            this.weapon = weapon;
            this.disguiseType = disguiseType;
        }
        
        public Builder healthRegen(boolean healthRegen) {
            this.healthRegen = healthRegen;
            return this;
        }
        
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public Builder unlockCost(int unlockCost) {
            this.unlockCost = unlockCost;
            return this;
        }
        
        public Builder perUseCost(int perUseCost) {
            this.perUseCost = perUseCost;
            return this;
        }
        
        public Builder health(int health) {
            this.health = health;
            return this;
        }
        
        public Builder addDamageImmunity(DamageCause damageCause) {
            this.damageImmunities.add(damageCause);
            return this;
        }
        
        public Builder addEffect(PotionEffectType type, int amplifier) {
            this.effects.add(new MutationEffect(type, amplifier));
            return this;
        }
        
        public Builder addItem(int slotOffset, ItemStack itemStack) {
            this.items.add(new MutationItem(slotOffset, itemStack));
            return this;
        }
        
        public MutationType create() {
            if (this.displayName == null || this.displayName.equals("")) {
                this.displayName = StringHelper.capitalizeEveryWord(this.id);
            }
            
            if (armorType == null) {
                armorType = ArmorType.NONE;
            }
            
            if (health == 0) {
                health = 20;
            }
            
            return new MutationType(id, displayName, armorType, weapon, disguiseType, unlockCost, perUseCost, health, healthRegen, damageImmunities, effects, items);
        }
    }
}