package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.helper.*;
import com.stardevllc.starcore.api.colors.ColorHandler;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starmclib.names.MaterialNames;
import com.stardevllc.starmclib.names.PotionNames;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MutateGui extends InventoryGUI {
    public MutateGui(SurvivalGames plugin, MutationBuilder builder) {
        super(Math.min(6, StandardMutations.values().length / 9 + 1), "&lMutate on " + builder.getTarget().getName() + " as...", builder.getPlayer().getUniqueId());
        GamePlayer player = builder.getPlayer();
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        PlayerMutations unlockedMutations = plugin.getUnlockedMutations(player.getUniqueId());
        
        if (!unlockedMutations.isUnlocked("pig_zombie")) {
            unlockedMutations.add(new UnlockedMutation(player.getUniqueId(), "pig_zombie", player.getNexusPlayer().getPlayerTime().getFirstJoined()));
        }
        
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        for (IMutationType type : StandardMutations.values()) {
            if (plugin.getDisabledMutations().contains(type)) {
                continue;
            }
            
            if (unlockedMutations.isUnlocked(type.getId()) && !game.getSettings().isUseAllMutations()) {
                continue;
            }
            
            List<String> rawImmunities = new ArrayList<>();
            if (type.getDamageImmunities().isEmpty()) {
                rawImmunities.add("None");
            } else {
                boolean handledExplosion = false;
                boolean handledFire = false;
                for (DamageCause damageImmunity : type.getDamageImmunities()) {
                    if (damageImmunity == DamageCause.BLOCK_EXPLOSION || damageImmunity == DamageCause.ENTITY_EXPLOSION) {
                        if (!handledExplosion) {
                            rawImmunities.add("Explosion");
                            handledExplosion = true;
                        }
                    } else if (damageImmunity == DamageCause.FIRE || damageImmunity == DamageCause.FIRE_TICK) {
                        if (!handledFire) {
                            rawImmunities.add("Fire");
                            handledFire = true;
                        }
                    } else {
                        rawImmunities.add(StringHelper.titlize(damageImmunity.name()));
                    }
                }
            }
            
            String damageImmunities = StringHelper.join(rawImmunities, ", ");
            
            List<String> lore = new ArrayList<>(List.of(
                    "&fArmor: &e" + StringHelper.titlize(type.getArmorType().name()),
                    "&fHealth: &e" + type.getHealth() / 2 + "❤", // u2764
                    "&fDamage Immunities: &e" + damageImmunities,
                    "",
                    "&6&lPotion Effects"
            ));
            
            if (type.getEffects().isEmpty()) {
                lore.add("&7- None");
            } else {
                for (MutationEffect effect : type.getEffects()) {
                    lore.add("&7- " + PotionNames.getDefaultName(effect.getPotionType()) + " " + RomanNumerals.decimalToRoman(effect.getAmplifier() + 1));
                }
            }
            
            lore.add("");
            lore.add("&6&lItems");
            lore.add("&7- Player Tracker (Target Only)");
            lore.add("&7- " + getItemName(type.getWeapon()));
            for (MutationItem item : type.getItems()) {
                String line = "&7- ";
                ItemStack itemStack = item.itemStack();
                
                if (itemStack == null) {
                    plugin.getLogger().severe("ItemStack is null for Mutation Item " + item.slotOffset() + " in type " + type.name());
                    continue;
                }
                
                if (itemStack.getAmount() > 1) {
                    line += itemStack.getAmount() + "x ";
                } 
                
                line += getItemName(itemStack);
                lore.add(line);
            }
            
            lore.add("");
            lore.add("&6&lAdditional Modifiers");
            if (type.getModifiers().isEmpty()) {
                lore.add("&7- None");
            } else {
                for (MutationModifier modifier : type.getModifiers()) {
                    lore.add("&7- " + modifier.getDisplayName());
                }
            }
            
            Button button = new Button()
                    .iconCreator(p ->
                            ItemBuilder.of(type.getIcon())
                                    .displayName("&e&l" + type.getDisplayName())
                                    .setLore(lore)
                                    .build()
                    )
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
    
    private String getItemName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta.getDisplayName() != null && !itemMeta.getDisplayName().isEmpty()) {
                return ColorHandler.stripColor(itemMeta.getDisplayName());
            }
        }
        
        return ColorHandler.stripColor(MaterialNames.getDefaultName(itemStack.getType()));
    }
}
