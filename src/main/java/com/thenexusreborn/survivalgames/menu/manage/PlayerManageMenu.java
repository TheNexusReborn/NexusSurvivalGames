package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.itembuilder.ItemBuilder;
import com.stardevllc.itembuilder.XMaterial;
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
import org.bukkit.entity.Player;

public class PlayerManageMenu extends InventoryGUI implements UpdatingGUI {

    private SurvivalGames plugin;
    private GuiManager guiManager;
    private Game game;
    private SGPlayer player;
    private GamePlayer target;
    
    public PlayerManageMenu(SurvivalGames plugin, Game game, SGPlayer player, GamePlayer target) {
        super(1, "&e&lManage " + target.getName(), player.getUniqueId());
        this.plugin = plugin;
        this.game = game;
        this.player = player;
        this.target = target;
        this.guiManager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
    }

    @Override
    public void createItems() {
        if (target.getTeam() == GameTeam.SPECTATORS) {
            Button addButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.LIME_WOOL)
                                    .displayName("&a&lADD")
                                    .addLoreLine("&7&oAdds the player to the game")
                                    .build())
                    .consumer(e -> game.addAsTribute(player, target, null, 0));
            addElement(addButton);
        }

        if (target.getTeam() != GameTeam.SPECTATORS) {
            Button removeButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.RED_WOOL)
                                    .displayName("&c&lREMOVE")
                                    .addLoreLine("&7&oRemoves the player from the game")
                                    .build())
                    .consumer(e -> game.removeFromGame(player, target));
            addElement(removeButton);
        }

        if (target.getTeam() == GameTeam.SPECTATORS && target.isSpectatorByDeath() && target.getMostRecentDeath() != null) {
            Button reviveButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.NETHER_STAR)
                                    .displayName("&e&lREVIVE")
                                    .addLoreLine("&7&oRevives the player")
                                    .build())
                    .consumer(e -> game.revivePlayer(player, target, null, 0));
            addElement(reviveButton);
        }

        if (target.getTeam() == GameTeam.SPECTATORS) {
            Button mutateButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.ROTTEN_FLESH)
                                    .displayName("&d&lMUTATE")
                                    .addLoreLine("&7&oMutate the player based on settings")
                                    .build())
                    .consumer(e -> guiManager.openGUI(new ManageMutateMenu(plugin, player, game, new MutationBuilder(target)), (Player) e.getWhoClicked()));
            addElement(mutateButton);
        }
    }
}
