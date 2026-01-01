package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.ui.GuiManager;
import com.stardevllc.starcore.api.ui.element.button.Button;
import com.stardevllc.starcore.api.ui.gui.InventoryGUI;
import com.stardevllc.starcore.api.ui.gui.UpdatingGUI;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.MutationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ManageMutateTargetMenu extends InventoryGUI implements UpdatingGUI {
    
    private GuiManager manager;
    
    private Game game;
    private MutationBuilder builder;
    private ManageMutateMenu previousMenu;
    
    public ManageMutateTargetMenu(SurvivalGames plugin, SGPlayer actor, Game game, MutationBuilder builder, ManageMutateMenu previousMenu) {
        super("&lSelect " + builder.getPlayer().getName() + "'s target", actor.getUniqueId(), new String[]{
                "TTTTTTTTT",
                "TTTTTTTTT",
                "TTTTTTTTT"
        });
        setDynamicChar('T');
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        this.game = game;
        this.builder = builder;
        this.previousMenu = previousMenu;
    }
    
    @Override
    public void createItems() {
        this.dynamicElements.clear();
        if (game != null) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getToggleValue("vanish")) {
                    continue;
                }
                
                if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                    continue;
                }
                
                Button button = new Button(p -> {
                    ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(gamePlayer.getUniqueId()));
                    ItemMeta meta = skull.getItemMeta();
                    meta.setDisplayName(StarColors.color(gamePlayer.getDisplayName()));
                    skull.setItemMeta(meta);
                    return skull;
                }, e -> {
                    builder.setTarget(gamePlayer);
                    manager.openGUI(previousMenu, e.getWhoClicked());
                });
                addElement(button);
            }
        }
    }
}
