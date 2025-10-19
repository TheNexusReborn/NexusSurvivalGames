package com.thenexusreborn.survivalgames.items;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.staritems.model.CustomItem;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ToHubItem extends CustomItem {
    public ToHubItem(SurvivalGames plugin) {
        super(plugin, "tohub", ItemBuilders.of(SMaterial.OAK_DOOR).displayName("&e&lReturn to Hub &7(Right Click)"));
        
        addEventHandler(PlayerInteractEvent.class, e -> {
            if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }

            Player player = e.getPlayer();
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }

            SGUtils.sendToHub(player);
            sgPlayer.sendMessage("&6&l>> &eSending you to the hub.");
        });
    }
}
