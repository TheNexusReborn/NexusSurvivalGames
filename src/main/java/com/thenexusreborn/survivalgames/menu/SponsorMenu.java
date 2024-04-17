package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starcore.gui.element.button.Button;
import com.stardevllc.starcore.gui.gui.InventoryGUI;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.sponsoring.SponsorCategory;
import com.thenexusreborn.survivalgames.sponsoring.SponsorManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SponsorMenu extends InventoryGUI {
    public SponsorMenu(SurvivalGames plugin, GamePlayer actor, GamePlayer target) {
        super(1, "&lSponsor " + target.getName());

        SGPlayer actorPlayer = plugin.getPlayerRegistry().get(actor.getUniqueId());
        SGPlayer targetPlayer = plugin.getPlayerRegistry().get(target.getUniqueId());

        if (actorPlayer.getGame() != targetPlayer.getGame()) {
            actor.sendMessage(MsgType.ERROR + "You and the target are not in the same game.");
            return;
        }
        
        Game game = actorPlayer.getGame();
        
        int creditCost = game.getSettings().getSponsorCreditCost();
        int scoreCost = game.getSettings().getSponsorScoreCost();
    
        SponsorManager sponsorManager = game.getSponsorManager();
        for (SponsorCategory<?> category : sponsorManager.getCategories()) {
            ItemBuilder iconBuilder = ItemBuilder.of(category.getIcon());
            iconBuilder.displayName("&a&l" + category.getName());
            List<String> lore = new LinkedList<>();
            lore.add("&ePossible Items");
            category.getListOfEntries().forEach(entry -> lore.add("  &a- " + entry));
            lore.add("");
            lore.add("&6&lLeft Click &fto use " + creditCost + " &3Credits");
            lore.add("&6&lRight Click &fto use " + scoreCost + " &dScore");
            iconBuilder.setLore(lore);
            
            Button button = new Button().iconCreator(p -> iconBuilder.build()).consumer(e -> {
                String currency;
                int cost;
                
                if (e.getClick() == ClickType.LEFT) {
                    currency = "credits";
                    cost = creditCost;
                } else if (e.getClick() == ClickType.RIGHT) {
                    currency = "sg_score";
                    cost = scoreCost;
                } else {
                    return;
                }
                
                if (actor.hasSponsored()) {
                    actor.sendMessage(MsgType.WARN + "You can only sponsor once per game.");
                    return;
                }

                if (!actor.isSpectatorByDeath()) {
                    actor.sendMessage(MsgType.WARN + "You can only sponsor players if you died.");
                    return;
                }

                int amount;
                if (currency.equalsIgnoreCase("credits")) {
                    amount = (int) actor.getBalance().getCredits();
                } else {
                    amount = actor.getStats().getScore();
                }
                if (amount < cost) {
                    actor.sendMessage(MsgType.WARN + "You do not have enough " + currency + ".");
                    return;
                }
                
                if (currency.equalsIgnoreCase("credits")) {
                    actor.getBalance().addCredits(-cost);
                } else {
                    actor.getStats().addScore(-cost);
                }
                actor.setSponsored(true);

                Object chosen = category.getEntries().get(new Random().nextInt(category.getEntries().size()));
                category.apply(Bukkit.getPlayer(target.getUniqueId()), chosen);
                game.sendMessage("&6&l>> " + target.getColoredName() + " &awas &lSPONSORED &aa(n) &l" + category.getName() + " item &aby " + actor.getColoredName() + "&a!");
                
                actor.getStats().addSponsoredOthers(1);
                target.getStats().addSponsorsReceived(1);
            });
            addElement(button);
        }
    }
}