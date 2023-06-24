package com.thenexusreborn.survivalgames.menu;

import com.starmediadev.starui.GuiManager;
import com.starmediadev.starui.element.button.Button;
import com.starmediadev.starui.gui.InventoryGUI;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamMenu extends InventoryGUI {
    public TeamMenu(SurvivalGames plugin, GameTeam team) {
        super(3, MCUtils.color(team.getColor() + team.getName()));
        GuiManager manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        if (plugin.getGame() != null) {
            for (GamePlayer player : plugin.getGame().getPlayers().values()) {
                if (!player.getToggleValue("vanish")) {
                    if (player.getTeam() == team) {
                        ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(player.getUniqueId()));
                        ItemMeta meta = skull.getItemMeta();
                        meta.setDisplayName(MCUtils.color(player.getDisplayName()));
                        skull.setItemMeta(meta);
                        Button button = new Button().creator(p -> skull).consumer(e -> manager.openGUI(new PlayerMenu(plugin, player), (Player) e.getWhoClicked()));
                        addElement(button);
                    }
                }
            }
        }
    }
}
