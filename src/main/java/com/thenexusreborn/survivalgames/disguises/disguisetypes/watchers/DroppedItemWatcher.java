package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DroppedItemWatcher extends FlagWatcher {

    public DroppedItemWatcher(Disguise disguise) {
        super(disguise);
    }

    public ItemStack getItemStack() {
        return (ItemStack) getValue(10, new ItemStack(Material.STONE));
    }

    public void setItemStack(ItemStack item) {
        setValue(10, item);
        sendData(10);
    }

}
