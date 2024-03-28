package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;
import com.thenexusreborn.survivalgames.disguises.utilities.DisguiseUtilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FallingBlockWatcher extends FlagWatcher {

    private ItemStack block;

    public FallingBlockWatcher(Disguise disguise) {
        super(disguise);
    }

    @Override
    public FallingBlockWatcher clone(Disguise disguise) {
        FallingBlockWatcher watcher = (FallingBlockWatcher) super.clone(disguise);
        watcher.setBlock(getBlock());
        return watcher;
    }

    public ItemStack getBlock() {
        return block;
    }

    public void setBlock(ItemStack block) {
        this.block = block;
        if (block.getType() == null || block.getType() == Material.AIR) {
            block.setType(Material.STONE);
        }
        if (DisguiseAPI.isDisguiseInUse(getDisguise()) && getDisguise().getWatcher() == this) {
            DisguiseUtilities.refreshTrackers(getDisguise());
        }
    }
}
