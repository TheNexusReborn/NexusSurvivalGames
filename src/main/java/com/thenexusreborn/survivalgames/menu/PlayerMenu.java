package com.thenexusreborn.survivalgames.menu;

import com.starmediadev.starui.GuiManager;
import com.starmediadev.starui.element.Element;
import com.starmediadev.starui.element.button.Button;
import com.starmediadev.starui.gui.InventoryGUI;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerMenu extends InventoryGUI {
    public PlayerMenu(SurvivalGames plugin, GamePlayer player) {
        super(1, "&lMenu " + player.getName());

        Button teleportButton = new Button().creator(p -> ItemBuilder.start(Material.ENDER_PEARL).displayName("&e&lTeleport").build())
                .consumer(e -> {
                    e.getWhoClicked().teleport(Bukkit.getPlayer(player.getUniqueId()).getLocation());
                    e.getWhoClicked().sendMessage(MCUtils.color(MsgType.INFO + "Teleported to " + player.getColoredName() + "&e."));
                });

        List<String> vitalsLore = player.getMenuVitals().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element vitalsElement = new Element().creator(p -> ItemBuilder.start(Material.POTION).data((short) 0).displayName("&e&lVitals").lore(vitalsLore).build());

        Button viewInventoryButton = new Button().creator(p -> ItemBuilder.start(Material.ENDER_CHEST).displayName("Inventory").lore("&c&lNOT YET IMPLEMENTED").build())
                .consumer(e -> e.getWhoClicked().sendMessage(MCUtils.color(MsgType.WARN + "This feature is not yet implemented.")));

        Button sponsorButton = new Button().creator(p -> ItemBuilder.start(Material.CHEST).displayName("&e&lSponsor").build())
                .consumer(e -> {
                    GuiManager manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
                    GamePlayer gp = plugin.getGame().getPlayer(e.getWhoClicked().getUniqueId());
                    if (!plugin.getGame().getSettings().isAllowSponsoring()) {
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
        Element statsElement = new Element().creator(p -> ItemBuilder.start(Material.PAPER).displayName("&e&lStats").lore(statsLore).build());

        Element typeElement = new Element().creator(p -> ItemBuilder.start(Material.SKULL_ITEM).data((short) 1).displayName(player.getTeam().getColor() + "&l" + player.getTeam().name().substring(0, player.getTeam().name().length() - 1)).build());

        addElement(teleportButton);
        addElement(vitalsElement);
        addElement(viewInventoryButton);
        if (plugin.getGame().getSettings().isAllowSponsoring() && player.getToggleValue("allowsponsors") && player.getTeam() == GameTeam.TRIBUTES) {
            addElement(sponsorButton);
        }
        addElement(statsElement);
        addElement(typeElement);
    }
}