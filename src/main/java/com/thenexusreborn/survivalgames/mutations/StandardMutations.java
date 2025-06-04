package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.helper.StringHelper;
import com.stardevllc.starcore.api.XMaterial;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starcore.utils.ArmorSet;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.DisguiseType;
import com.thenexusreborn.survivalgames.mutations.impl.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static com.thenexusreborn.survivalgames.mutations.MutationItem.UNBREAKABLE_GOLD_SWORD;

public enum StandardMutations implements IMutationType {
    PIG_ZOMBIE(
            "pig_zombie", //id
            PigZombieMutation.class, //class 
            DisguiseType.PIG_ZOMBIE, //Disguise Type
            XMaterial.PORKCHOP, //Icon
            ArmorSet.CHAINMAIL, //Armor
            UNBREAKABLE_GOLD_SWORD.build(), //Weapon 
            0, //Unlock cost
            20, //Health
            List.of(DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA), //Damage Immunities
            List.of(new MutationEffect(PotionEffectType.SPEED, 1)), //Potion Effects
            List.of(), //Additional Items
            List.of(MutationModifier.NO_HEALTH_REGEN) //Modifiers
    ),
    
    ZOMBIE(
            "zombie", //id
            ZombieMutation.class, //class 
            DisguiseType.ZOMBIE, //Disguise Type
            XMaterial.ROTTEN_FLESH, //Icon
            ArmorSet.IRON, //Armor
            UNBREAKABLE_GOLD_SWORD.build(), //Weapon 
            3000, //Unlock cost
            20, //Health
            List.of(), //Damage Immunities
            List.of(new MutationEffect(PotionEffectType.SLOW, 0)), //Potion Effects
            List.of(), //Additional Items
            List.of() //Modifiers
    ),
    
    ENDERMAN(
            "enderman", //id
            EndermanMutation.class, //class 
            DisguiseType.ENDERMAN, //Disguise Type
            XMaterial.ENDER_PEARL, //Icon
            ArmorSet.LEATHER, //Armor
            UNBREAKABLE_GOLD_SWORD.build(), //Weapon 
            5000, //Unlock cost
            30, //Health
            List.of(DamageCause.FALL, DamageCause.PROJECTILE), //Damage Immunities
            List.of(), //Potion Effects
            List.of(new MutationItem(1, new ItemStack(Material.ENDER_PEARL, 32))), //Additional Items
            List.of(MutationModifier.ALLERGIC_TO_WATER) //Modifiers
    ),
    
    SKELETON(
            "skeleton", //id
            SkeletonMutation.class, //class 
            DisguiseType.SKELETON, //Disguise Type
            XMaterial.BOW, //Icon
            ArmorSet.LEATHER, //Armor
            ItemBuilder.of(XMaterial.WOODEN_SWORD).displayName("&fWooden Sword").build(), //Weapon 
            3000, //Unlock cost
            20, //Health
            List.of(), //Damage Immunities
            List.of(new MutationEffect(PotionEffectType.SPEED, 0)), //Potion Effects
            List.of(
                    new MutationItem(1, ItemBuilder.of(XMaterial.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).build()), 
                    new MutationItem(2, new ItemStack(Material.ARROW, 32))
            ), //Additional Items
            List.of(MutationModifier.FIFTY_PERCENT_INCREASED_DAMAGE) //Modifiers
    ),
    
    CHICKEN(
            "chicken", //id
            ChickenMutation.class, //class 
            DisguiseType.CHICKEN, //Disguise Type
            XMaterial.FEATHER, //Icon
            ArmorSet.GOLD, //Armor
            ItemBuilder.of(XMaterial.WOODEN_SWORD).displayName("&bEgg Launcher").addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).addEnchant(Enchantment.ARROW_DAMAGE, 1).build(), //Weapon 
            5000, //Unlock cost
            4, //Health
            List.of(DamageCause.FALL), //Damage Immunities
            List.of(), //Potion Effects
            List.of(
                    new MutationItem(1, SurvivalGames.chickenLaunchItem),
                    new MutationItem(2, SurvivalGames.chickenParachuteItem)
            ), //Additional Items
            List.of() //Modifiers
    ),
    
    CREEPER(
            "creeper", //id
            CreeperMutation.class, //class 
            DisguiseType.CREEPER, //Disguise Type
            XMaterial.GUNPOWDER, //Icon
            ArmorSet.CHAINMAIL, //Armor
            new ItemStack(Material.WOOD_AXE), //Weapon 
            5000, //Unlock cost
            10, //Health
            List.of(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION), //Damage Immunities
            List.of(new MutationEffect(PotionEffectType.SPEED, 1)), //Potion Effects
            List.of(
                    new MutationItem(1, new ItemStack(Material.TNT, 32)),
                    new MutationItem(2, SurvivalGames.creeperBombItem
                    )
            ), //Additional Items
            List.of() //Modifiers
    ),
    ;
    
//  Blaze, Spider, Witch and Wither Skeleton
    
    private final String id;
    private final String displayName;
    private final Class<? extends Mutation> clazz;
    private final DisguiseType disguiseType;
    private final XMaterial icon;
    private final ArmorSet armorType;
    private final ItemStack weapon;
    private final int unlockCost;
    private final int health;
    private final List<DamageCause> damageImmunities;
    private final List<MutationEffect> effects;
    private final List<MutationItem> items;
    private final List<MutationModifier> modifiers;
    
    StandardMutations(String id, String displayName, Class<? extends Mutation> clazz, DisguiseType disguiseType, XMaterial icon, ArmorSet armorType, ItemStack weapon, int unlockCost, int health, List<DamageCause> damageImmunities, List<MutationEffect> effects, List<MutationItem> items, List<MutationModifier> modifiers) {
        this.id = id;
        this.displayName = displayName;
        this.clazz = clazz;
        this.disguiseType = disguiseType;
        this.icon = icon;
        this.armorType = armorType;
        this.weapon = weapon;
        this.unlockCost = unlockCost;
        this.health = health;
        this.damageImmunities = damageImmunities;
        this.effects = effects;
        this.items = items;
        this.modifiers = modifiers;
        
        IMutationType.REGISTRY.register(this);
    }
    
    StandardMutations(String id, Class<? extends Mutation> clazz, DisguiseType disguiseType, XMaterial icon, ArmorSet armorType, ItemStack weapon, int unlockCost, int health, List<DamageCause> damageImmunities, List<MutationEffect> effects, List<MutationItem> items, List<MutationModifier> modifiers) {
        this(id, StringHelper.titlize(id), clazz, disguiseType, icon, armorType, weapon, unlockCost, health, damageImmunities, effects, items, modifiers);
    }
    
    public static IMutationType getType(String name) {
        for (IMutationType type : values()) {
            if (type.getId().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public ArmorSet getArmorType() {
        return armorType;
    }
    
    @Override
    public XMaterial getIcon() {
        return icon;
    }
    
    @Override
    public Class<? extends Mutation> getClazz() {
        return clazz;
    }
    
    @Override
    public ItemStack getWeapon() {
        return weapon;
    }
    
    @Override
    public DisguiseType getDisguiseType() {
        return disguiseType;
    }
    
    @Override
    public int getUnlockCost() {
        return unlockCost;
    }
    
    @Override
    public int getHealth() {
        return health;
    }
    
    @Override
    public List<DamageCause> getDamageImmunities() {
        return damageImmunities;
    }
    
    @Override
    public List<MutationEffect> getEffects() {
        return effects;
    }
    
    @Override
    public List<MutationItem> getItems() {
        return items;
    }
    
    @Override
    public List<MutationModifier> getModifiers() {
        return modifiers;
    }
}