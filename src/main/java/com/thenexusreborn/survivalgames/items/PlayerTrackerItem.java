package com.thenexusreborn.survivalgames.items;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.staritems.model.CustomItem;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class PlayerTrackerItem extends CustomItem {
    public PlayerTrackerItem(SurvivalGames plugin) {
        super(plugin, "playertracker", ItemBuilders.of(SMaterial.COMPASS).displayName("&fPlayer Tracker"));
    }
}
