package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starcore.api.itembuilder.ItemBuilders;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.starmclib.XMaterial;
import org.bukkit.inventory.ItemStack;

public final class MutationItem {
    public static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilders.of(XMaterial.GOLDEN_SWORD).displayName("&fUnbreakable Golden Sword").unbreakable(true);
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
