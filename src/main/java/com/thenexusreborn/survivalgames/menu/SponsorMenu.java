package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.menu.element.button.*;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.sponsoring.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class SponsorMenu extends Menu {
    public SponsorMenu(SurvivalGames plugin, GamePlayer actor, GamePlayer target) {
        super(plugin, "sponsor", "&lSponsor " + target.getName(), 1);
        
        int creditCost = plugin.getGame().getSettings().getSponsorCreditCost();
        int scoreCost = plugin.getGame().getSettings().getSponsorScoreCost();
    
        SponsorManager sponsorManager = plugin.getGame().getSponsorManager();
        for (SponsorCategory<?> category : sponsorManager.getCategories()) {
            ItemBuilder iconBuilder = ItemBuilder.start(category.getIcon());
            iconBuilder.displayName("&a&l" + category.getName());
            List<String> lore = new LinkedList<>();
            lore.add("&ePossible Items");
            category.getListOfEntries().forEach(entry -> lore.add("  &a- " + entry));
            lore.add("");
            lore.add("&6&lLeft Click &fto use " + creditCost + " &3Credits");
            lore.add("&6&lRight Click &fto use " + scoreCost + " &dScore");
            iconBuilder.lore(lore);
    
            Button button = new Button(iconBuilder.build());
            button.setLeftClickAction(new SponsorAction(plugin, category, actor, target, "credits", creditCost));
            button.setRightClickAction(new SponsorAction(plugin, category, actor, target, "sg_score", scoreCost));
            addElement(button);
        }
    }
    
    private static class SponsorAction implements ButtonAction {
        
        private SurvivalGames plugin;
        private SponsorCategory<?> category;
        private GamePlayer actor, target;
        private String currency;
        private int cost;
    
        public SponsorAction(SurvivalGames plugin, SponsorCategory<?> category, GamePlayer actor, GamePlayer target, String currency, int cost) {
            this.plugin = plugin;
            this.category = category;
            this.actor = actor;
            this.target = target;
            this.currency = currency;
            this.cost = cost;
        }
    
        @Override
        public void onClick(Player player, Menu menu, ClickType click) {
            if (actor.hasSponsored()) {
                actor.sendMessage(MsgType.WARN + "You can only sponsor once per game.");
                return;
            }
    
            if (!actor.isSpectatorByDeath()) {
                actor.sendMessage(MsgType.WARN + "You can only sponsor players if you died.");
                return;
            }
    
            int amount = actor.getStatValue(currency).getAsInt();
            if (amount < cost) {
                actor.sendMessage(MsgType.WARN + "You do not have enough " + NexusAPI.getApi().getStatRegistry().get(currency).getDisplayName() + ".");
                return;
            }
    
            actor.changeStat(currency, cost, StatOperator.SUBTRACT).push();
            actor.setSponsored(true);
    
            Object chosen = category.getEntries().get(new Random().nextInt(category.getEntries().size()));
            category.apply(Bukkit.getPlayer(target.getUniqueId()), chosen);
            plugin.getGame().sendMessage("&6&l>> " + target.getColoredName() + " &awas &lSPONSORED &aa(n) &l" + category.getName() + " item &aby " + actor.getColoredName() + "&a!");
            actor.changeStat("sg_sponsored_others", 1, StatOperator.ADD).push();
            target.changeStat("sg_sponsors_received", 1, StatOperator.ADD).push();
        }
    }
}
