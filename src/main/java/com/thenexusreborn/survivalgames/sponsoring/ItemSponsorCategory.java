package com.thenexusreborn.survivalgames.sponsoring;

import com.cryptomorin.xseries.XMaterial;
import com.stardevllc.starcore.color.ColorUtils;
import com.thenexusreborn.survivalgames.loot.LootItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class ItemSponsorCategory extends SponsorCategory<LootItem> {
    public ItemSponsorCategory(String name, XMaterial icon) {
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
            entriesList.add(ChatColor.stripColor(ColorUtils.color(entry.getName())));
        }
        return entriesList;
    }
}
