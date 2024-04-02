package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starlib.Pair;
import com.stardevllc.starui.element.Element;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.MutationType;
import com.thenexusreborn.survivalgames.mutations.PlayerMutations;
import com.thenexusreborn.survivalgames.mutations.UnlockedMutation;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MutateGui extends InventoryGUI {
    public MutateGui(SurvivalGames plugin, GamePlayer player) {
        super(3, "&lMutate as...");

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
                        if (game == null) {
                            player.sendMessage(MsgType.WARN + "You cannot mutate, You are not in a game.");
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
                                player.getStats().addMutationPasses(-1);
                            }
                        } else {
                            player.sendMessage(MsgType.INFO + "&aYou got lucky and you did not use a pass for this mutation!");
                        }

                        player.getStats().addTimesMutated(1);

                        Mutation mutation = Mutation.createInstance(game, type, player.getUniqueId(), gamePlayer.getKiller());
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
