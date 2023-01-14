package com.thenexusreborn.survivalgames.sponsoring;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.loot.LootItem;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class ItemSponsorCategory extends SponsorCategory<LootItem> {
    public ItemSponsorCategory(String name, Material icon) {
        super(name, icon);
    }
    
    @Override
    public void apply(Player player, Object entry) {
        player.getInventory().addItem(((LootItem) entry).getItemStack());
    }
    
    @Override
    public List<String> getListOfEntries() {
        List<String> entriesList = new LinkedList<>();
        for (LootItem entry : this.getEntries()) {
            entriesList.add(ChatColor.stripColor(MCUtils.color(entry.getName())));
        }
        return entriesList;
    }
}
