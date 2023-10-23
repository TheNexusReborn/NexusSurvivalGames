package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.*;
import me.firestar311.starlib.api.Pair;
import me.firestar311.starui.element.Element;
import me.firestar311.starui.element.button.Button;
import me.firestar311.starui.gui.InventoryGUI;
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
        setElement(0, purchasedIndex++, new Element().iconCreator(p -> ItemBuilder.start(Material.EMERALD_BLOCK).displayName("&a&lPurchased").build()));
        setElement(0, purchasedIndex++, new Element().iconCreator(p -> glassPane.build()));
        setElement(1, availableIndex++, new Element().iconCreator(p -> ItemBuilder.start(Material.GOLD_BLOCK).displayName("&e&lAvailable").build()));
        setElement(1, availableIndex++, new Element().iconCreator(p -> glassPane.build()));
        setElement(2, lockedIndex++, new Element().iconCreator(p -> ItemBuilder.start(Material.REDSTONE_BLOCK).displayName("&4&lLocked").build()));
        setElement(2, lockedIndex++, new Element().iconCreator(p -> glassPane.build()));
    
        //TODO The Item Lore for the different categories
        for (String m : purchased) {
            MutationType type = MutationType.getType(m);
            Button button = new Button().iconCreator(p -> ItemBuilder.start(type.getIcon()).displayName("&a&l" + type.getDisplayName()).build())
                    .consumer(e -> {
                        Game game = plugin.getGame();
                        if (game == null) {
                            player.sendMessage(MsgType.WARN + "You cannot mutate, a game is not running.");
                            return;
                        }

                        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                        Pair<Boolean, String> canMutateResult = gamePlayer.canMutate();
                        if (!canMutateResult.firstValue()) {
                            player.sendMessage(MsgType.WARN + canMutateResult.secondValue());
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

                        Mutation mutation = Mutation.createInstance(type, player.getUniqueId(), gamePlayer.getKiller());
                        gamePlayer.setMutation(mutation);
                        mutation.startCountdown();
                        e.getWhoClicked().closeInventory();
                    });
            setElement(0, purchasedIndex++, button);
        }
    
        for (String m : available) {
            MutationType type = MutationType.getType(m);
            Button button = new Button()
                    .iconCreator(p -> ItemBuilder.start(type.getIcon()).displayName("&e&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits", "&6&lLeft Click &fto purchase.").build())
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
            Element element = new Element().iconCreator(p -> ItemBuilder.start(type.getIcon()).displayName("&c&l" + type.getDisplayName()).lore("&eCost: &f" + type.getUnlockCost() + " &3Credits").build());
            setElement(2, lockedIndex++, element);
        }
    }
}
