package com.thenexusreborn.survivalgames.items;

import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class PlayerTrackerItem extends CustomItem {
    public PlayerTrackerItem(SurvivalGames plugin) {
        super(plugin, "playertracker", ItemBuilder.of(XMaterial.COMPASS).displayName("&fPlayer Tracker"));
    }
}
