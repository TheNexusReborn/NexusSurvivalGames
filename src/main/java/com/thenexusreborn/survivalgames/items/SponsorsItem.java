package com.thenexusreborn.survivalgames.items;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.staritems.model.CustomItem;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SponsorsItem extends CustomItem {
    public SponsorsItem(SurvivalGames plugin) {
        super(plugin, "sponsors_item", ItemBuilders.of(SMaterial.GLOWSTONE_DUST).displayName("Sponsors"));
        
        addEventHandler(PlayerInteractEvent.class, e -> {
            if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                return;
            }
            
            Player player = e.getPlayer();
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }
            
            Lobby lobby = sgPlayer.getLobby();
            if (lobby == null) {
                return;
            }
            
            LobbyPlayer lobbyPlayer = sgPlayer.getLobbyPlayer();
            if (lobbyPlayer == null) {
                return;
            }
            
            boolean sponsorsValue = lobbyPlayer.getToggleValue("allowsponsors");
            lobbyPlayer.getPlayer().setToggleValue("allowsponsors", !sponsorsValue);
        });
    }
}
