package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import org.bukkit.inventory.ItemStack;

public final class MutationItem {
    public static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilder.of(XMaterial.GOLDEN_SWORD).displayName("&fUnbreakable Golden Sword").unbreakable(true);
    private final int slotOffset;
    private ItemBuilder itemBuilder;
    private CustomItem customItem;
    
    public MutationItem(int slotOffset, ItemStack itemStack) {
        this.slotOffset = slotOffset;
        this.itemBuilder = ItemBuilder.fromItemStack(itemStack);
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
