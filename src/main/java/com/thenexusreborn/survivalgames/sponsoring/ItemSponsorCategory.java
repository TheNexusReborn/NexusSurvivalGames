package com.thenexusreborn.survivalgames.sponsoring;

import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class ItemSponsorCategory extends SponsorCategory<LootItem> {
    public ItemSponsorCategory(String name, SMaterial icon) {
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
            entriesList.add(ChatColor.stripColor(StarColors.color(entry.getName())));
        }
        return entriesList;
    }
}
