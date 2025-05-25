package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public record MutationItem(int slotOffset, ItemStack itemStack) {
    public static final ItemBuilder UNBREAKABLE_GOLD_SWORD = ItemBuilder.of(XMaterial.GOLDEN_SWORD).displayName("&fUnbreakable Goldlen Sword").unbreakable(true);
}
