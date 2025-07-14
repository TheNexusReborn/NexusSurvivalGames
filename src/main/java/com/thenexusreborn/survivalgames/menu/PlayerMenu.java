package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starmclib.XMaterial;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.Element;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.menu.manage.PlayerManageMenu;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class PlayerMenu extends InventoryGUI implements UpdatingGUI {

    private SurvivalGames plugin;
    private GamePlayer player;

    public PlayerMenu(SurvivalGames plugin, UUID playerUUID, GamePlayer player) {
        super(1, "&lMenu " + player.getName(), playerUUID);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void createItems() {
        SGPlayer actorPlayer = plugin.getPlayerRegistry().get(this.playerUUID);
        Game game = actorPlayer.getGame();
        GamePlayer actorGamePlayer = actorPlayer.getGamePlayer();
        
        if (game == null) {
            return;
        }
        
        if (actorGamePlayer == null) {
            return;
        }
        
        GuiManager manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

        if (actorGamePlayer.getTeam() == GameTeam.SPECTATORS || actorGamePlayer.getRank().ordinal() <= Rank.ADMIN.ordinal()) {
            Button teleportButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.ENDER_PEARL).displayName("&e&lTeleport").build())
                    .consumer(e -> {
                        e.getWhoClicked().teleport(Bukkit.getPlayer(player.getUniqueId()).getLocation());
                        SGPlayer actor = plugin.getPlayerRegistry().get(e.getWhoClicked().getUniqueId());
                        actor.getGamePlayer().setPosition(e.getWhoClicked().getLocation());
                        e.getWhoClicked().sendMessage(StarColors.color(MsgType.INFO + "Teleported to " + player.getColoredName() + "&e."));
                    });
            addElement(teleportButton);

            if (player.getTeam() != GameTeam.SPECTATORS) {
                List<String> vitalsLore = player.getMenuVitals().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
                Element vitalsElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.GLASS_BOTTLE).displayName("&e&lVitals").setLore(vitalsLore).build());
                addElement(vitalsElement);

                Button viewInventoryButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.ENDER_CHEST).displayName("Inventory").addLoreLine("&c&lNOT YET IMPLEMENTED").build())
                        .consumer(e -> e.getWhoClicked().sendMessage(StarColors.color(MsgType.WARN + "This feature is not yet implemented.")));
                addElement(viewInventoryButton);
            }

            boolean sponsoringAllowed = sgPlayer.getGame().getSettings().isAllowSponsoring();

            if (sponsoringAllowed && player.getToggleValue("allowsponsors") && player.getTeam() == GameTeam.TRIBUTES) {
                Button sponsorButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.CHEST).displayName("&e&lSponsor").build())
                        .consumer(e -> {
                            SGPlayer actor = plugin.getPlayerRegistry().get(e.getWhoClicked().getUniqueId());
                            if (actor.getGame() == null) {
                                e.getWhoClicked().sendMessage(MsgType.WARN.format("You are not in a game."));
                                return;
                            }

                            GamePlayer gp = actor.getGamePlayer();
                            if (!sgPlayer.getGame().getSettings().isAllowSponsoring()) {
                                gp.sendMessage(MsgType.WARN + "Sponsoring is disabled for this game.");
                                return;
                            }

                            if (!player.getToggleValue("allowsponsors")) {
                                gp.sendMessage(MsgType.WARN + "That player has sponsors disabled.");
                                return;
                            }

                            manager.openGUI(new SponsorMenu(plugin, gp, player), e.getWhoClicked());
                        });

                addElement(sponsorButton);
            }
        }

        List<String> statsLore = player.getMenuStats().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element statsElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.PAPER).displayName("&e&lStats").setLore(statsLore).build());
        addElement(statsElement);

        Element typeElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.PLAYER_HEAD).displayName(player.getTeam().getColor() + "&l" + player.getTeam().name().substring(0, player.getTeam().name().length() - 1)).build());
        addElement(typeElement);

        if (actorPlayer.getNexusPlayer().getRank().ordinal() > Rank.ADMIN.ordinal()) {
            return;
        }
        
        Button manageButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.REDSTONE_BLOCK).displayName("&c&lMANAGE").build()).consumer(e -> manager.openGUI(new PlayerManageMenu(plugin, actorPlayer.getGame(), new PlayerManageBuilder(actorPlayer, player)), e.getWhoClicked()));
        addElement(manageButton);
    }
}