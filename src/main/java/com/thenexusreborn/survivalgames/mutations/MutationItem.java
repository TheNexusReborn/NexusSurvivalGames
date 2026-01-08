package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.itembuilder.common.ItemBuilder;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.staritems.model.CustomItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class MutationItem {
    public static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilders.of(SMaterial.GOLDEN_SWORD).displayName("&fGolden Sword").addEnchant(Enchantment.DURABILITY, 10).unbreakable(true);
    private final int slotOffset;
    private ItemBuilder itemBuilder;
    private CustomItem customItem;
    
    public MutationItem(int slotOffset, ItemStack itemStack) {
        this.slotOffset = slotOffset;
        this.itemBuilder = ItemBuilders.of(itemStack);
    }
    
    public MutationItem(int slotOffset, ItemBuilder itemBuilder) {
        this.slotOffset = slotOffset;
        this.itemBuilder = itemBuilder;
    }
    
    public MutationItem(int slotOffset, CustomItem customItem) {
        this.slotOffset = slotOffset;
        this.customItem = customItem;
    }
    
    public int slotOffset() {
        return slotOffset;
    }
    
    public ItemStack itemStack() {
        if (itemBuilder != null) {
            return itemBuilder.build();
        }
        
        if (customItem != null) {
            return customItem.toItemStack();
        }
        
        return null;
    }
}
