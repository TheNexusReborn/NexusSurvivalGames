package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.nexuscore.menu.element.Element;
import com.thenexusreborn.nexuscore.menu.element.button.Button;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerMenu extends Menu {
    public PlayerMenu(SurvivalGames plugin, GamePlayer player) {
        super(plugin, "player", "&lMenu " + player.getName(), 1);
        
        Button teleportButton = new Button(ItemBuilder.start(Material.ENDER_PEARL).displayName("&e&lTeleport").build());
        teleportButton.setLeftClickAction((p, menu, click) -> {
            p.teleport(Bukkit.getPlayer(player.getUniqueId()).getLocation());
            p.sendMessage(MCUtils.color(MsgType.INFO + "Teleported to " + player.getColoredName() + "&e."));
        });
    
        List<String> vitalsLore = player.getMenuVitals().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element vitalsElement = new Element(ItemBuilder.start(Material.POTION).data((short) 0).displayName("&e&lVitals").lore(vitalsLore).build());
        
        Button viewInventoryButton = new Button(ItemBuilder.start(Material.ENDER_CHEST).displayName("Inventory").lore("&c&lNOT YET IMPLEMENTED").build());
        viewInventoryButton.setLeftClickAction((p, menu, click) -> p.sendMessage(MCUtils.color(MsgType.WARN + "This feature is not yet implemented.")));
        
        Button sponsorButton = new Button(ItemBuilder.start(Material.CHEST).displayName("&e&lSponsor").build());
        sponsorButton.setLeftClickAction((p, menu, click) -> {
           GamePlayer gp = plugin.getGame().getPlayer(p.getUniqueId());
           if (!plugin.getGame().getSettings().isAllowSponsoring()) {
               gp.sendMessage(MsgType.WARN + "Sponsoring is disabled for this game.");
               return;
           }
           
           if (!player.getToggleValue("allowsponsors")) {
               gp.sendMessage(MsgType.WARN + "That player has sponsors disabled.");
               return;
           }
           p.openInventory(new SponsorMenu(plugin, gp, player).getInventory());
        });
    
        List<String> statsLore = player.getMenuStats().stream().map(line -> "  " + line).collect(Collectors.toCollection(LinkedList::new));
        Element statsElement = new Element(ItemBuilder.start(Material.PAPER).displayName("&e&lStats").lore(statsLore).build());
        
        Element typeElement = new Element(ItemBuilder.start(Material.SKULL_ITEM).data((short) 1).displayName(player.getTeam().getColor() + "&l" + player.getTeam().name().substring(0, player.getTeam().name().length() - 1)).build());
        
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