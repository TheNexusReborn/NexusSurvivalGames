package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.itembuilder.ItemBuilder;
import com.stardevllc.itembuilder.XMaterial;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.conversation.SelectLootTablePrompt;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.PlayerManageBuilder;
import com.thenexusreborn.survivalgames.mutations.MutationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PlayerManageMenu extends InventoryGUI implements UpdatingGUI {

    private SurvivalGames plugin;
    private GuiManager guiManager;
    private Game game;
    
    private PlayerManageBuilder manageBuilder;
    
    public PlayerManageMenu(SurvivalGames plugin, Game game, PlayerManageBuilder manageBuilder) {
        super(1, "&e&lManage " + manageBuilder.getTarget(), manageBuilder.getActor().getUniqueId());
        this.plugin = plugin;
        this.game = game;
        this.manageBuilder = manageBuilder;
        this.guiManager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
    }

    @Override
    public void createItems() {
        GamePlayer target = manageBuilder.getTarget();
        SGPlayer actor = manageBuilder.getActor();
        if (target.getTeam() == GameTeam.SPECTATORS) {
            Button addButton = new Button().iconCreator(p -> {
                        ItemBuilder ib = ItemBuilder.of(XMaterial.LIME_WOOL)
                                .displayName("&a&lADD")
                                .addLoreLine("&d&oAdds the player to the game");
                        if (this.manageBuilder.getLootTable() == null) {
                            ib.addLoreLine("&7&oThis will add " + target.getName() + " to the game with no items");
                        } else {
                            ib.addLoreLine("&7&oThis will add " + target.getName() + " to the game with " + this.manageBuilder.getNumberOfItems() + " item(s) from the " + this.manageBuilder.getLootTable().getName() + " loot table");
                        }
                        
                        ib.addLoreLine("");
                        ib.addLoreLine("&6&lLeft Click &fto add " + target.getName() + " with current settings");
                        ib.addLoreLine("&6&lRight Click &fto configure the loot table and number of items");
                        return ib.build();
                    })
                    .consumer(e -> {
                        if (e.getClick() == ClickType.LEFT) {
                            game.addAsTribute(actor, target, manageBuilder.getLootTable(), manageBuilder.getNumberOfItems());
                        } else if (e.getClick() == ClickType.RIGHT) {
                            e.getWhoClicked().closeInventory();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> new ConversationFactory(plugin).withFirstPrompt(new SelectLootTablePrompt(plugin, this, manageBuilder)).buildConversation((Player) e).begin(), 1L);
                        }
                    });
            addElement(addButton);
        }

        if (target.getTeam() != GameTeam.SPECTATORS) {
            Button removeButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.RED_WOOL)
                                    .displayName("&c&lREMOVE")
                                    .addLoreLine("&7&oRemoves the player from the game")
                                    .build())
                    .consumer(e -> game.removeFromGame(actor, target));
            addElement(removeButton);
        }

        if (target.getTeam() == GameTeam.SPECTATORS && target.isSpectatorByDeath() && target.getMostRecentDeath() != null) {
            Button reviveButton = new Button().iconCreator(p -> {
                        ItemBuilder ib = ItemBuilder.of(XMaterial.NETHER_STAR)
                                .displayName("&e&lREVIVE")
                                .addLoreLine("&d&oRevives the player");
                        if (this.manageBuilder.getLootTable() == null) {
                            ib.addLoreLine("&7&oThis will revive " + target.getName() + " with no items");
                        } else {
                            ib.addLoreLine("&7&oThis will revive " + target.getName() + " with " + this.manageBuilder.getNumberOfItems() + " item(s) from the " + this.manageBuilder.getLootTable().getName() + " loot table");
                        }

                        ib.addLoreLine("");
                        ib.addLoreLine("&6&lLeft Click &fto revive " + target.getName() + " with current settings");
                        ib.addLoreLine("&6&lRight Click &fto configure the loot table and number of items");
                        return ib.build();
                    })
                    .consumer(e -> {
                        if (e.getClick() == ClickType.LEFT) {
                            game.revivePlayer(actor, target, manageBuilder.getLootTable(), manageBuilder.getNumberOfItems());
                        } else if (e.getClick() == ClickType.RIGHT) {
                            e.getWhoClicked().closeInventory();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> new ConversationFactory(plugin).withFirstPrompt(new SelectLootTablePrompt(plugin, this, manageBuilder)).buildConversation((Player) e).begin(), 1L);
                        }
                    });
            addElement(reviveButton);
        }

        if (target.getTeam() == GameTeam.SPECTATORS) {
            Button mutateButton = new Button().iconCreator(p ->
                            ItemBuilder.of(XMaterial.ROTTEN_FLESH)
                                    .displayName("&d&lMUTATE")
                                    .addLoreLine("&7&oMutate the player based on settings")
                                    .build())
                    .consumer(e -> guiManager.openGUI(new ManageMutateMenu(plugin, actor, game, new MutationBuilder(target)), e.getWhoClicked()));
            addElement(mutateButton);
        }
    }
}
