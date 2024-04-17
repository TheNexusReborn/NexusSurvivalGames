package com.thenexusreborn.survivalgames.menu;

import com.cryptomorin.xseries.XMaterial;
import com.stardevllc.starcore.gui.GuiManager;
import com.stardevllc.starcore.gui.element.Element;
import com.stardevllc.starcore.gui.element.button.Button;
import com.stardevllc.starcore.gui.gui.InventoryGUI;
import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerMenu extends InventoryGUI {
    public PlayerMenu(SurvivalGames plugin, GamePlayer player) {
        super(1, "&lMenu " + player.getName());

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

        Button teleportButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.ENDER_PEARL).displayName("&e&lTeleport").build())
                .consumer(e -> {
                    e.getWhoClicked().teleport(Bukkit.getPlayer(player.getUniqueId()).getLocation());
                    e.getWhoClicked().sendMessage(ColorUtils.color(MsgType.INFO + "Teleported to " + player.getColoredName() + "&e."));
                });

        List<String> vitalsLore = player.getMenuVitals().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element vitalsElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.GLASS_BOTTLE).displayName("&e&lVitals").setLore(vitalsLore).build());

        Button viewInventoryButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.ENDER_CHEST).displayName("Inventory").addLoreLine("&c&lNOT YET IMPLEMENTED").build())
                .consumer(e -> e.getWhoClicked().sendMessage(ColorUtils.color(MsgType.WARN + "This feature is not yet implemented.")));

        Button sponsorButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.CHEST).displayName("&e&lSponsor").build())
                .consumer(e -> {
                    GuiManager manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
                    GamePlayer gp = sgPlayer.getGamePlayer();
                    if (!sgPlayer.getGame().getSettings().isAllowSponsoring()) {
                        gp.sendMessage(MsgType.WARN + "Sponsoring is disabled for this game.");
                        return;
                    }

                    if (!player.getToggleValue("allowsponsors")) {
                        gp.sendMessage(MsgType.WARN + "That player has sponsors disabled.");
                        return;
                    }

                    manager.openGUI(new SponsorMenu(plugin, gp, player), (Player) e.getWhoClicked());
                });
        List<String> statsLore = player.getMenuStats().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element statsElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.PAPER).displayName("&e&lStats").setLore(statsLore).build());

        Element typeElement = new Element().iconCreator(p -> ItemBuilder.of(XMaterial.PLAYER_HEAD).displayName(player.getTeam().getColor() + "&l" + player.getTeam().name().substring(0, player.getTeam().name().length() - 1)).build());

        addElement(teleportButton);
        addElement(vitalsElement);
        addElement(viewInventoryButton);
        if (sgPlayer.getGame().getSettings().isAllowSponsoring() && player.getToggleValue("allowsponsors") && player.getTeam() == GameTeam.TRIBUTES) {
            addElement(sponsorButton);
        }
        addElement(statsElement);
        addElement(typeElement);
    }
}