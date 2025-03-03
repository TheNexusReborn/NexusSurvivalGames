package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.colors.StarColors;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TeamMenu extends InventoryGUI implements UpdatingGUI {
    
    private GuiManager manager;
    
    private SurvivalGames plugin;
    private Game game;
    private GameTeam team;
    
    public TeamMenu(SurvivalGames plugin, GameTeam team, Game game, UUID player) {
        super(3, StarColors.color(team.getColor() + team.getName()), player);
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        this.plugin = plugin;
        this.game = game;
        this.team = team;
    }

    @Override
    public void createItems() {
        if (game != null) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (!gamePlayer.getToggleValue("vanish")) {
                    if (gamePlayer.getTeam() == team) {
                        Button button = new Button().iconCreator(p -> {
                            ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(gamePlayer.getUniqueId()));
                            ItemMeta meta = skull.getItemMeta();
                            meta.setDisplayName(StarColors.color(gamePlayer.getDisplayName()));
                            skull.setItemMeta(meta);
                            return skull;
                        }).consumer(e -> manager.openGUI(new PlayerMenu(plugin, this.playerUUID, gamePlayer), (Player) e.getWhoClicked()));
                        addElement(button);
                    }
                }
                    meta.setDisplayName(StarColors.color(gamePlayer.getColoredName()));
            }
        }
    }
}
