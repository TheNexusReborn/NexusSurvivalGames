package com.thenexusreborn.survivalgames.mutations;

import org.bukkit.inventory.ItemStack;

public class MutationItem {
    private final int slotOffset;
    private final ItemStack itemStack;
    
    public MutationItem(int slotOffset, ItemStack itemStack) {
        this.slotOffset = slotOffset;
        this.itemStack = itemStack;
    }
    
    public int getSlotOffset() {
        return slotOffset;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
}
