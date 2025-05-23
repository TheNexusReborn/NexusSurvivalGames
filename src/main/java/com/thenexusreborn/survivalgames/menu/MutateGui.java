package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.helper.Pair;
import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.starui.element.Element;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Bukkit;

import java.util.*;

public class MutateGui extends InventoryGUI {
    public MutateGui(SurvivalGames plugin, MutationBuilder mutationBuilder) {
        super(3, "&lMutate on " + mutationBuilder.getTarget().getName() + " as...");

        GamePlayer player = mutationBuilder.getPlayer();
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        PlayerMutations unlockedMutations = plugin.getUnlockedMutations(player.getUniqueId());

        if (!unlockedMutations.isUnlocked("pig_zombie")) {
            unlockedMutations.add(new UnlockedMutation(player.getUniqueId(), "pig_zombie", player.getNexusPlayer().getPlayerTime().getFirstJoined()));
        }

        double credits = player.getBalance().getCredits();
    
        List<String> purchased = new ArrayList<>(), available = new ArrayList<>(), locked = new ArrayList<>();
        for (MutationType type : MutationType.TYPES) {
            if (game != null && game.getSettings().isUseAllMutations()) {
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
        
        ItemBuilder glassPane = ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).displayName("&f");
        setElement(0, purchasedIndex++, new Element().iconCreator(p -> ItemBuilder.of(XMaterial.EMERALD_BLOCK).displayName("&a&lPurchased").build()));
        setElement(0, purchasedIndex++, new Element().iconCreator(p -> glassPane.build()));
        setElement(1, availableIndex++, new Element().iconCreator(p -> ItemBuilder.of(XMaterial.GOLD_BLOCK).displayName("&e&lAvailable").build()));
        setElement(1, availableIndex++, new Element().iconCreator(p -> glassPane.build()));
        setElement(2, lockedIndex++, new Element().iconCreator(p -> ItemBuilder.of(XMaterial.REDSTONE_BLOCK).displayName("&4&lLocked").build()));
        setElement(2, lockedIndex++, new Element().iconCreator(p -> glassPane.build()));
    
        //TODO The Item Lore for the different categories
        for (String m : purchased) {
            MutationType type = MutationType.getType(m);
            Button button = new Button().iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&a&l" + type.getDisplayName()).build())
                    .consumer(e -> {
                        if (game == null) {
                            player.sendMessage(MsgType.WARN + "You cannot mutate, You are not in a game.");
                            return;
                        }

                        Pair<Boolean, String> canMutateResult = player.canMutate();
                        if (!canMutateResult.key()) {
                            player.sendMessage(MsgType.WARN + canMutateResult.value());
                            return;
                        }

                        if (mutationBuilder.isUsePass()) {
                            double passUseValue = new Random().nextDouble();
                            if (passUseValue <= game.getSettings().getPassUseChance()) {
                                if (!game.getSettings().isUnlimitedPasses()) {
                                    player.getStats().addMutationPasses(-1);
                                }
                            } else {
                                player.sendMessage(MsgType.INFO + "&2&lLUCKY! &aYou did not use a pass for this mutation!");
                            }
                        }

                        player.getStats().addTimesMutated(1);

                        Mutation mutation = Mutation.createInstance(game, type, player.getUniqueId(), mutationBuilder.getTarget().getUniqueId());
                        player.setMutation(mutation);
                        mutation.startCountdown();
                        e.getWhoClicked().closeInventory();
                    });
            setElement(0, purchasedIndex++, button);
        }
    
        for (String m : available) {
            MutationType type = MutationType.getType(m);
            Button button = new Button()
                    .iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&e&l" + type.getDisplayName()).setLore(List.of("&eCost: &f" + type.getUnlockCost() + " &3Credits", "&6&lLeft Click &fto purchase.")).build())
                    .consumer(e -> {
                        player.removeCredits(type.getUnlockCost());
                        UnlockedMutation unlockedMutation = new UnlockedMutation(player.getUniqueId(), type.getId(), System.currentTimeMillis());
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> NexusReborn.getPrimaryDatabase().saveSilent(unlockedMutation));
                        unlockedMutations.add(unlockedMutation);
                        player.sendMessage(MsgType.INFO + "You bought the mutation &b" + type.getDisplayName() + " &efor &b" + type.getUnlockCost() + " &ecredits.");
                        e.getWhoClicked().closeInventory();
                    });
            setElement(1, availableIndex++, button);
        }
    
        for (String m : locked) {
            MutationType type = MutationType.getType(m);
            Element element = new Element().iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&c&l" + type.getDisplayName()).addLoreLine("&eCost: &f" + type.getUnlockCost() + " &3Credits").build());
            setElement(2, lockedIndex++, element);
        }
    }
}
