package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.smaterial.ArmorSet;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starlib.registry.StringRegistry;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.DisguiseType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IMutationType {
    StringRegistry<IMutationType> REGISTRY = new StringRegistry<>(null, String::toLowerCase, IMutationType::getId, null, null);
    
    static IMutationType valueOf(String str) {
        return REGISTRY.get(str);
    }
    
    String name();
    
    String getId();
    
    String getDisplayName();
    
    ArmorSet getArmorType();
    
    SMaterial getIcon();
    
    Class<? extends Mutation> getClazz();
    
    ItemStack getWeapon();
    
    DisguiseType getDisguiseType();
    
    int getUnlockCost();
    
    int getHealth();
    
    List<DamageCause> getDamageImmunities();
    
    List<MutationEffect> getEffects();
    
    List<MutationItem> getItems();
    
    List<MutationModifier> getModifiers();
}
