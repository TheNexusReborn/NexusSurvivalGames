package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.nexuscore.menu.element.button.Button;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamMenu extends Menu {
    public TeamMenu(SurvivalGames plugin, GameTeam team) {
        super(plugin, "team", "", 3);
        
        if (plugin.getGame() != null) {
            for (GamePlayer player : plugin.getGame().getPlayers().values()) {
                if (player.getTeam() == team) {
                    ItemStack skull = SpigotUtils.getPlayerSkull(player.getNexusPlayer().getPlayer());
                    ItemMeta meta = skull.getItemMeta();
                    meta.setDisplayName(player.getNexusPlayer().getDisplayName());
                    skull.setItemMeta(meta);
                    Button button = new Button(skull);
                    button.setLeftClickAction((p, menu, clickType) -> {
                        p.teleport(player.getNexusPlayer().getPlayer().getLocation()); //TODO This will be replaced with a menu at some point
                    });
                }
            }
        }
    }
}
