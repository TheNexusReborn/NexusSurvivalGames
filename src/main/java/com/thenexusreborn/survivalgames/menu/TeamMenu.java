package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.nexuscore.menu.element.button.Button;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamMenu extends Menu {
    public TeamMenu(SurvivalGames plugin, GameTeam team) {
        super(plugin, "team", MCUtils.color(team.getColor() + team.getName()), 3);
        
        if (plugin.getGame() != null) {
            for (GamePlayer player : plugin.getGame().getPlayers().values()) {
                if (!player.getToggleValue("vanish")) {
                    if (player.getTeam() == team) {
                        ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(player.getUniqueId()));
                        ItemMeta meta = skull.getItemMeta();
                        meta.setDisplayName(MCUtils.color(player.getDisplayName()));
                        skull.setItemMeta(meta);
                        Button button = new Button(skull);
                        button.setLeftClickAction((p, menu, clickType) -> p.openInventory(new PlayerMenu(plugin, player).getInventory()));
                        addElement(button);
                    }
                }
            }
        }
    }
}
