package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.helper.Pair;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Bukkit;

import java.util.Random;

public class MutateGui extends InventoryGUI {
    public MutateGui(SurvivalGames plugin, MutationBuilder builder) {
        super(Math.min(6, MutationType.values().length / 9 + 1), "&lMutate on " + builder.getTarget().getName() + " as...", builder.getPlayer().getUniqueId());
        GamePlayer player = builder.getPlayer();
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        PlayerMutations unlockedMutations = plugin.getUnlockedMutations(player.getUniqueId());
        
        if (!unlockedMutations.isUnlocked("pig_zombie")) {
            unlockedMutations.add(new UnlockedMutation(player.getUniqueId(), "pig_zombie", player.getNexusPlayer().getPlayerTime().getFirstJoined()));
        }
        
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        for (MutationType type : MutationType.values()) {
            if (unlockedMutations.isUnlocked(type.getId()) && !game.getSettings().isUseAllMutations()) {
                continue;
            }
            
            Button button = new Button().iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&e&l" + type.getDisplayName()).build())
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
                        
                        if (builder.isUsePass()) {
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
                        
                        Mutation mutation = Mutation.createInstance(game, type, player.getUniqueId(), builder.getTarget().getUniqueId());
                        player.setMutation(mutation);
                        mutation.startCountdown();
                        e.getWhoClicked().closeInventory();
                    });
            addElement(button);
        }
    }
}
