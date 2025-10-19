package com.thenexusreborn.survivalgames.items;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starcore.api.ui.GuiManager;
import com.stardevllc.staritems.model.CustomItem;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.menu.ModifierMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameModifierItem extends CustomItem {
    public GameModifierItem(SurvivalGames plugin) {
        super(plugin, "gamemodifiervote", ItemBuilders.of(SMaterial.REDSTONE_TORCH)
                .displayName("&e&lGAME MODIFIERS").addLoreLine("&7Vote for different game modifiers"));
        GuiManager guiManager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        addEventHandler(PlayerInteractEvent.class, e -> {
            if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }
            
            Player player = e.getPlayer();
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }
            
            Lobby lobby = sgPlayer.getLobby();
            if (lobby == null) {
                MsgType.WARN.send(sgPlayer.getSpigotPlayer(), "You can only use this item in the lobby.");
                return;
            }
            
            guiManager.openGUI(new ModifierMenu(sgPlayer, lobby), player);
        });
    }
}
