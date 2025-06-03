package com.thenexusreborn.survivalgames.items;

import com.stardevllc.starcore.api.XMaterial;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.types.PlayerEvent;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.menu.TeamMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class GameTeamBook extends CustomItem {
    
    private final GuiManager manager;
    
    public GameTeamBook(SurvivalGames plugin, GameTeam gameTeam) {
        super(plugin, gameTeam.getName().toLowerCase() + "book", ItemBuilder.of(XMaterial.ENCHANTED_BOOK)
                .displayName(gameTeam.getColor() + gameTeam.getName() + " &7&o(Right Click)"));

        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
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

            manager.openGUI(new TeamMenu(plugin, gameTeam, game, player.getUniqueId()), player);
        });
    }
}
