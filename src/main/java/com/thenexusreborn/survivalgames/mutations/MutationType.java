package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.helper.StringHelper;
import com.stardevllc.itembuilder.ItemBuilder;
import com.stardevllc.itembuilder.XMaterial;
import com.stardevllc.starcore.utils.ArmorSet;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.DisguiseType;
import com.thenexusreborn.survivalgames.mutations.impl.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static com.stardevllc.starcore.utils.ArmorSet.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;
import static org.bukkit.potion.PotionEffectType.*;

public class MutationType {
    private static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilder.of(XMaterial.GOLDEN_SWORD).unbreakable(true);
    
    public static final Set<MutationType> TYPES = new HashSet<>();
    
    public static final MutationType PIG_ZOMBIE = new Builder("pig_zombie", PigZombieMutation.class, DisguiseType.PIG_ZOMBIE).icon(XMaterial.PORKCHOP).armor(LEATHER).addDamageImmunity(FIRE).weapon(UNBREAKABLE_GOLD_SWORD.build()).addDamageImmunity(FIRE_TICK).addEffect(SPEED, 1).healthRegen(false).create();
    public static final MutationType ZOMBIE = new Builder("zombie", ZombieMutation.class, DisguiseType.ZOMBIE).icon(XMaterial.ROTTEN_FLESH).armor(IRON).weapon(UNBREAKABLE_GOLD_SWORD.build()).unlockCost(3000).addEffect(SLOW, 0).create();
    public static final MutationType ENDERMAN = new Builder("enderman", EndermanMutation.class, DisguiseType.ENDERMAN).armor(LEATHER).icon(XMaterial.ENDER_PEARL).weapon(UNBREAKABLE_GOLD_SWORD.build()).unlockCost(5000).addItem(1, new ItemStack(Material.ENDER_PEARL, 32)).addDamageImmunity(FALL).create();
    public static final MutationType SKELETON = new Builder("skeleton", SkeletonMutation.class, DisguiseType.SKELETON).armor(LEATHER).icon(XMaterial.BOW).weapon(new ItemStack(Material.WOOD_SWORD)).unlockCost(3000).addItem(1, ItemBuilder.of(XMaterial.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).unbreakable(true).build()).addItem(2, new ItemStack(Material.ARROW, 32)).addEffect(SPEED, 0).create();
    public static final MutationType CHICKEN = new Builder("chicken", ChickenMutation.class, DisguiseType.CHICKEN).icon(XMaterial.FEATHER).weapon(ItemBuilder.of(XMaterial.WOODEN_SWORD).displayName("&bEgg Launcher").addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).addEnchant(Enchantment.ARROW_DAMAGE, 1).build()).health(4).unlockCost(5000).addItem(1, ItemBuilder.of(XMaterial.SLIME_BALL).displayName("&bChicken Launch").build()).addItem(2, ItemBuilder.of(XMaterial.FEATHER).displayName("&bChicken Chute").build()).addDamageImmunity(FALL).create();
    public static final MutationType CREEPER = new Builder("creeper", CreeperMutation.class, DisguiseType.CREEPER).icon(XMaterial.GUNPOWDER).armor(CHAINMAIL).weapon(new ItemStack(Material.TNT, 32)).health(10).unlockCost(3000).addDamageImmunity(BLOCK_EXPLOSION).addDamageImmunity(ENTITY_EXPLOSION).addEffect(SPEED, 1).addItem(1, ItemBuilder.of(XMaterial.GUNPOWDER).displayName("&cSuicide").build()).create();
    
//  (These are MVP only) Blaze, Spider, Witch and Wither Skeleton
    
    protected String id;
    protected String displayName;
    protected Class<? extends Mutation> clazz;
    protected XMaterial icon;
    protected ArmorSet armorType;
    protected ItemStack weapon;
    protected DisguiseType disguiseType;
    protected int unlockCost;
    protected int health;
    protected boolean healthRegen;
    protected List<DamageCause> damageImmunities;
    protected List<MutationEffect> effects;
    protected List<MutationItem> items;
    
    MutationType(String id, String displayName, Class<? extends Mutation> clazz, XMaterial icon, ArmorSet armorType, ItemStack weapon, DisguiseType disguiseType, int unlockCost, int health, boolean healthRegen, List<DamageCause> damageImmunities, List<MutationEffect> effects, List<MutationItem> items) {
        this.id = id;
        this.displayName = displayName;
        this.clazz = clazz;
        this.icon = icon;
        this.armorType = armorType;
        this.weapon = weapon;
        this.disguiseType = disguiseType;
        this.unlockCost = unlockCost;
        this.health = health;
        this.healthRegen = healthRegen;
        this.damageImmunities = damageImmunities;
        this.effects = effects;
        this.items = items;
        TYPES.add(this);
    }
    
    public static MutationType getType(String name) {
        for (MutationType type : new HashSet<>(TYPES)) {
            if (type.getId().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ArmorSet getArmorType() {
        return armorType;
    }
    
    public XMaterial getIcon() {
        return icon;
    }
    
    public Class<? extends Mutation> getClazz() {
        return clazz;
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
        private Class<? extends Mutation> clazz;
        private XMaterial icon;
        private ArmorSet armorType;
        private ItemStack weapon;
        private DisguiseType disguiseType;
        private int unlockCost;
        private int health;
        private boolean healthRegen = true;
        private List<DamageCause> damageImmunities = new ArrayList<>();
        private List<MutationEffect> effects = new ArrayList<>();
        private List<MutationItem> items = new ArrayList<>();
        
        public Builder(String id, Class<? extends Mutation> clazz, DisguiseType disguiseType) {
            this.id = id;
            this.disguiseType = disguiseType;
            this.clazz = clazz;
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
        
        public Builder icon(XMaterial icon) {
            this.icon = icon;
            return this;
        }
        
        public Builder armor(ArmorSet armorType) {
            this.armorType = armorType;
            return this;
        }
        
        public Builder weapon(ItemStack weapon) {
            this.weapon = weapon;
            return this;
        }
        
        public MutationType create() {
            if (this.displayName == null || this.displayName.isEmpty()) {
                this.displayName = StringHelper.titlize(this.id);
            }
            
            if (armorType == null) {
                armorType = ArmorSet.NONE;
            }
            
            if (health == 0) {
                health = 20;
            }
            
            return new MutationType(id, displayName, clazz, icon, armorType, weapon, disguiseType, unlockCost, health, healthRegen, damageImmunities, effects, items);
        }
    }

    public boolean healthRegen() {
        return healthRegen;
    }
}