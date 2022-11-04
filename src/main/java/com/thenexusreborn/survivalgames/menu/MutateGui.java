package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.menu.element.Element;
import com.thenexusreborn.nexuscore.menu.element.button.Button;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Material;

import java.util.*;

public class MutateGui extends Menu {
    public MutateGui(SurvivalGames plugin, NexusPlayer player) {
        super(plugin, "mutateas", "&lMutate as...", 3);

        PlayerMutations unlockedMutations = plugin.getUnlockedMutations(player.getUniqueId());

        if (!unlockedMutations.isUnlocked("pig_zombie")) {
            unlockedMutations.add(new UnlockedMutation(player.getUniqueId(), "pig_zombie", player.getStats().getValue("firstjoined").getAsLong()));
        }

        double credits = player.getStats().getValue("credits").getAsDouble();
    
        List<String> purchased = new ArrayList<>(), available = new ArrayList<>(), locked = new ArrayList<>();
        for (MutationType type : MutationType.TYPES) {
            if (unlockedMutations.isUnlocked(type.getId().toLowerCase())) {
                purchased.add(type.getId());
            } else if (type.getUnlockCost()<= credits) {
                available.add(type.getId());
            } else {
                locked.add(type.getId());
            }
        }
    
        int purchasedIndex = 0, availableIndex = 9, lockedIndex = 18;
        
        ItemBuilder glassPane = ItemBuilder.start(Material.STAINED_GLASS_PANE).displayName("&f").data((short) 7);
        setElement(purchasedIndex++, new Element(ItemBuilder.start(Material.EMERALD_BLOCK).displayName("&a&lPurchased").build()));
        setElement(purchasedIndex++, new Element(glassPane.build()));
        setElement(availableIndex++, new Element(ItemBuilder.start(Material.GOLD_BLOCK).displayName("&e&lAvailable").build()));
        setElement(availableIndex++, new Element(glassPane.build()));
        setElement(lockedIndex++, new Element(ItemBuilder.start(Material.REDSTONE_BLOCK).displayName("&4&lLocked").build()));
        setElement(lockedIndex++, new Element(glassPane.build()));
    
        //TODO The Item Lore for the different categories
        for (String m : purchased) {
            MutationType type = MutationType.getType(m);
            Button button = new Button(ItemBuilder.start(type.getIcon()).displayName("&a&l" + type.getDisplayName()).build());
            button.setLeftClickAction((p, menu, click) -> {
                Game game = plugin.getGame();
                if (game == null) {
                    player.sendMessage(MsgType.WARN + "You cannot mutate, a game is not running.");
                    return;
                }
    
                GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                
                if (gamePlayer.hasMutated()) {
                    player.sendMessage(MsgType.WARN + "You have already mutated, you cannot mutate again.");
                    return;
                }
                
                if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                    player.sendMessage(MsgType.WARN + "You must be a spectator to mutate.");
                    return;
                }
    
                DeathInfo deathInfo = gamePlayer.getDeathInfo();
                if (deathInfo == null) {
                    player.sendMessage(MsgType.WARN + "You can only mutate if you have died.");
                    return;
                }
                
                if (deathInfo.getType() != DeathType.PLAYER) {
                    player.sendMessage(MsgType.WARN + "You can only mutate if you died to a player.");
                    return;
                }
                
                DeathInfoPlayerKill playerDeath = (DeathInfoPlayerKill) deathInfo;
                UUID killerUUID = playerDeath.getKiller();
                GamePlayer killer = game.getPlayer(killerUUID);
                if (killer == null) {
                    player.sendMessage(MsgType.WARN + "Your killer left, you cannot mutate.");
                    return;
                }
                
                if (killer.getTeam() != GameTeam.TRIBUTES) {
                    player.sendMessage(MsgType.WARN + "Your killer has died, you cannot mutate.");
                    return;
                }
                
                int passes = player.getStats().getValue("sg_mutation_passes").getAsInt();
                
                if (passes <= 0 && !game.getSettings().isUnlimitedPasses()) {
                    player.sendMessage(MsgType.WARN + "You do not have any mutation passes.");
                    return;
                }
                
                double passUseValue = new Random().nextDouble();
                if (passUseValue <= game.getSettings().getPassUseChance()) {
                    player.getStats().change("sg_mutation_passes", 1, StatOperator.SUBTRACT);
                } else {
                    player.sendMessage(MsgType.INFO + "&aYou got lucky and you did not use a pass for this mutation!");
                }
                
                player.getStats().change("sg_times_mutated", 1, StatOperator.ADD);
    
                Mutation mutation = Mutation.createInstance(type, player.getUniqueId(), killerUUID);
                gamePlayer.setMutation(mutation);
                mutation.startCountdown();
                p.closeInventory();
            });
            setElement(purchasedIndex++, button);
        }
    
        for (String m : available) {
            MutationType type = MutationType.getType(m);
            Button button = new Button(ItemBuilder.start(type.getIcon()).displayName("&e&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits", "&6&lLeft Click &fto purchase.").build());
            button.setLeftClickAction((p, menu, click) -> {
                player.removeCredits(type.getUnlockCost());
                UnlockedMutation unlockedMutation = new UnlockedMutation(player.getUniqueId(), type.getId(), System.currentTimeMillis());
                NexusAPI.getApi().getThreadFactory().runAsync(() -> NexusAPI.getApi().getPrimaryDatabase().push(unlockedMutation));
                NexusAPI.getApi().getNetworkManager().send("unlockmutation", player.getUniqueId().toString(), unlockedMutation.getType(), unlockedMutation.getTimestamp() + "");
                unlockedMutations.add(unlockedMutation);
                player.sendMessage(MsgType.INFO + "You bought the mutation &b" + type.getDisplayName() + " &efor &b" + type.getUnlockCost() + " &ecredits.");
                p.closeInventory();
            });
            setElement(availableIndex++, button);
        }
    
        for (String m : locked) {
            MutationType type = MutationType.getType(m);
            Element element = new Element(ItemBuilder.start(type.getIcon()).displayName("&c&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits").build());
            setElement(lockedIndex++, element);
        }
    }
}
