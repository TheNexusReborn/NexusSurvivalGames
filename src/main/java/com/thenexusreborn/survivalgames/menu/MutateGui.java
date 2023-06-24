package com.thenexusreborn.survivalgames.menu;

import com.starmediadev.starui.element.Element;
import com.starmediadev.starui.element.button.Button;
import com.starmediadev.starui.gui.InventoryGUI;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Material;

import java.util.*;

public class MutateGui extends InventoryGUI {
    public MutateGui(SurvivalGames plugin, GamePlayer player) {
        super(3, "&lMutate as...");

        PlayerMutations unlockedMutations = plugin.getUnlockedMutations(player.getUniqueId());

        if (!unlockedMutations.isUnlocked("pig_zombie")) {
            unlockedMutations.add(new UnlockedMutation(player.getUniqueId(), "pig_zombie", player.getStatValue("firstjoined").getAsLong()));
        }

        double credits = player.getStatValue("credits").getAsDouble();
    
        List<String> purchased = new ArrayList<>(), available = new ArrayList<>(), locked = new ArrayList<>();
        for (MutationType type : MutationType.TYPES) {
            if (plugin.getGame() != null && plugin.getGame().getSettings().isUseAllMutations()) {
                purchased.add(type.getId());
            } else if (unlockedMutations.isUnlocked(type.getId().toLowerCase())) {
                purchased.add(type.getId());
            } else if (type.getUnlockCost()<= credits) {
                available.add(type.getId());
            } else {
                locked.add(type.getId());
            }
        }
    
        int purchasedIndex = 0, availableIndex = 0, lockedIndex = 0;
        
        ItemBuilder glassPane = ItemBuilder.start(Material.STAINED_GLASS_PANE).displayName("&f").data((short) 7);
        setElement(0, purchasedIndex++, new Element().creator(p -> ItemBuilder.start(Material.EMERALD_BLOCK).displayName("&a&lPurchased").build()));
        setElement(0, purchasedIndex++, new Element().creator(p -> glassPane.build()));
        setElement(1, availableIndex++, new Element().creator(p -> ItemBuilder.start(Material.GOLD_BLOCK).displayName("&e&lAvailable").build()));
        setElement(1, availableIndex++, new Element().creator(p -> glassPane.build()));
        setElement(2, lockedIndex++, new Element().creator(p -> ItemBuilder.start(Material.REDSTONE_BLOCK).displayName("&4&lLocked").build()));
        setElement(2, lockedIndex++, new Element().creator(p -> glassPane.build()));
    
        //TODO The Item Lore for the different categories
        for (String m : purchased) {
            MutationType type = MutationType.getType(m);
            Button button = new Button().creator(p -> ItemBuilder.start(type.getIcon()).displayName("&a&l" + type.getDisplayName()).build())
                    .consumer(e -> {
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

                        if (!gamePlayer.isSpectatorByDeath()) {
                            player.sendMessage(MsgType.WARN + "You can only mutate if you have died.");
                            return;
                        }

                        if (!gamePlayer.killedByPlayer()) {
                            player.sendMessage(MsgType.WARN + "You can only mutate if you died to a player.");
                            return;
                        }

                        UUID killerUUID = gamePlayer.getKiller();
                        GamePlayer killer = game.getPlayer(killerUUID);
                        if (killer == null) {
                            player.sendMessage(MsgType.WARN + "Your killer left, you cannot mutate.");
                            return;
                        }

                        if (killer.getTeam() != GameTeam.TRIBUTES) {
                            player.sendMessage(MsgType.WARN + "Your killer has died, you cannot mutate.");
                            return;
                        }

                        int passes = player.getStatValue("sg_mutation_passes").getAsInt();

                        if (passes <= 0 && !game.getSettings().isUnlimitedPasses()) {
                            player.sendMessage(MsgType.WARN + "You do not have any mutation passes.");
                            return;
                        }

                        if (player.getTotalTimesMutated() >= game.getSettings().getMaxMutationAmount()) {
                            player.sendMessage(MsgType.WARN + "You cannot mutate more than " + game.getSettings().getMaxMutationAmount() + " times in this game.");
                            return;
                        }

                        if (game.getTeamCount(GameTeam.MUTATIONS) >= game.getSettings().getMaxMutationsAllowed()) {
                            player.sendMessage(MsgType.WARN + "You cannot mutate as there are too many mutations in the game already.");
                            return;
                        }

                        double passUseValue = new Random().nextDouble();
                        if (passUseValue <= game.getSettings().getPassUseChance()) {
                            if (!game.getSettings().isUnlimitedPasses()) {
                                player.changeStat("sg_mutation_passes", 1, StatOperator.SUBTRACT);
                            }
                        } else {
                            player.sendMessage(MsgType.INFO + "&aYou got lucky and you did not use a pass for this mutation!");
                        }

                        player.changeStat("sg_times_mutated", 1, StatOperator.ADD);

                        Mutation mutation = Mutation.createInstance(type, player.getUniqueId(), killerUUID);
                        gamePlayer.setMutation(mutation);
                        mutation.startCountdown();
                        e.getWhoClicked().closeInventory();
                    });
            setElement(0, purchasedIndex++, button);
        }
    
        for (String m : available) {
            MutationType type = MutationType.getType(m);
            Button button = new Button()
                    .creator(p -> ItemBuilder.start(type.getIcon()).displayName("&e&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits", "&6&lLeft Click &fto purchase.").build())
                    .consumer(e -> {
                        player.removeCredits(type.getUnlockCost());
                        UnlockedMutation unlockedMutation = new UnlockedMutation(player.getUniqueId(), type.getId(), System.currentTimeMillis());
                        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> NexusAPI.getApi().getPrimaryDatabase().saveSilent(unlockedMutation));
                        NexusAPI.getApi().getNetworkManager().send("unlockmutation", player.getUniqueId().toString(), unlockedMutation.getType(), String.valueOf(unlockedMutation.getTimestamp()));
                        unlockedMutations.add(unlockedMutation);
                        player.sendMessage(MsgType.INFO + "You bought the mutation &b" + type.getDisplayName() + " &efor &b" + type.getUnlockCost() + " &ecredits.");
                        e.getWhoClicked().closeInventory();
                    });
            setElement(1, availableIndex++, button);
        }
    
        for (String m : locked) {
            MutationType type = MutationType.getType(m);
            Element element = new Element().creator(p -> ItemBuilder.start(type.getIcon()).displayName("&c&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits").build());
            setElement(2, lockedIndex++, element);
        }
    }
}
