package com.thenexusreborn.survivalgames.items;

import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.types.PlayerEvent;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ToHubItem extends CustomItem {
    public ToHubItem(SurvivalGames plugin) {
        super(plugin, "tohub", ItemBuilder.of(XMaterial.OAK_DOOR).displayName("&e&lReturn to Hub &7(Right Click)"));
        
        addEventHandler(PlayerEvent.INTERACT, e -> {
            if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }

            Player player = e.getPlayer();
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }

            Game game = sgPlayer.getGame();

            if (game == null) {
                return;
            }

            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                return;
            }

            SGUtils.sendToHub(player);
            gamePlayer.sendMessage("&6&l>> &eSending you to the hub.");
        });
    }
}
