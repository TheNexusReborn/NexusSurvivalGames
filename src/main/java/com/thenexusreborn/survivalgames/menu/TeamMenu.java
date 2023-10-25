package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import me.firestar311.starui.GuiManager;
import me.firestar311.starui.element.button.Button;
import me.firestar311.starui.gui.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamMenu extends InventoryGUI {
    public TeamMenu(SurvivalGames plugin, GameTeam team) {
        super(3, MCUtils.color(team.getColor() + team.getName()));
        GuiManager manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        if (plugin.getGame() != null) {
            int index = 0;
            for (GamePlayer player : plugin.getGame().getPlayers().values()) {
                if (!player.getToggleValue("vanish")) {
                    if (player.getTeam() == team) {
                        plugin.getLogger().info("Player is of team " + team.name());
                        Button button = new Button().iconCreator(p -> {
                            ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(player.getUniqueId()));
                            ItemMeta meta = skull.getItemMeta();
                            meta.setDisplayName(MCUtils.color(player.getDisplayName()));
                            skull.setItemMeta(meta);
                            return skull;
                        }).consumer(e -> manager.openGUI(new PlayerMenu(plugin, player), (Player) e.getWhoClicked()));
                        setElement(0, index++, button);
                        plugin.getLogger().info("Set element.");
                    }
                }
            }
        }
    }
}
