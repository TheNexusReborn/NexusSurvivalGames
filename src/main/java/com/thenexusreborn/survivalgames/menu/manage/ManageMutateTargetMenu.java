package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.v1_8_R1.itembuilder.SkullItemBuilder;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.mutations.MutationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ManageMutateTargetMenu extends InventoryGUI implements UpdatingGUI {
    
    private GuiManager manager;
    
    private Game game;
    private MutationBuilder builder;
    private ManageMutateMenu previousMenu;
    
    public ManageMutateTargetMenu(SurvivalGames plugin, SGPlayer actor, Game game, MutationBuilder builder, ManageMutateMenu previousMenu) {
        super(3, "&lSelect " + builder.getPlayer().getName() + "'s target", actor.getUniqueId());
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        this.game = game;
        this.builder = builder;
        this.previousMenu = previousMenu;
    }

    @Override
    public void createItems() {
        if (game != null) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (!gamePlayer.getToggleValue("vanish")) {
                    if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                        Button button = new Button().iconCreator(p -> {
                            SkullItemBuilder skullItemBuilder = new SkullItemBuilder()
                                    .owner(gamePlayer.getName())
                                    .displayName(gamePlayer.getDisplayName());
                            
                            return skullItemBuilder.build();
                        }).consumer(e -> {
                            builder.setTarget(gamePlayer);
                            manager.openGUI(previousMenu, (Player) e.getWhoClicked());
                        });
                        addElement(button);
                    }
                }
            }
        }
    }
}
