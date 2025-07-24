package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starlib.helper.StringHelper;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starmclib.XMaterial;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.game.GameModifier;
import com.thenexusreborn.survivalgames.game.GameModifierStatus;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class ModifierMenu extends InventoryGUI implements UpdatingGUI {
    
    private SGPlayer player;
    private Lobby lobby;
    
    public ModifierMenu(SGPlayer player, Lobby lobby) {
        super(Math.min(6, GameModifier.values().length / 9 + 1), "Game Modifiers", player.getUniqueId());
        this.player = player;
        this.lobby = lobby;
    }
    
    @Override
    public void createItems() {
        lobby.getMode().getModifiers().forEach((modifier, status) -> {
            if (status != GameModifierStatus.ALLOWED) {
                return;
            }
            
            List<String> loreLines = new ArrayList<>();
            loreLines.add("&fCurrent Value: &e" + modifier.getValueFunction().apply(lobby.getGameSettings()));
            loreLines.add("&6&lDESCRIPTION:");
            modifier.getDescription().forEach(descLine -> loreLines.add("  &f" + descLine));
            loreLines.add("&6&lVOTES:");
            loreLines.add("  &aYes: &e" + lobby.getModifierYesVotes(modifier));
            loreLines.add("  &cNo: &e" + lobby.getModifierNoVotes(modifier));
            loreLines.add(" ");
            loreLines.add("&6&lLeft Click &fto vote &aYES");
            loreLines.add("&6&lRight Click &fto vote &cNO");
            
            Button button = new Button(p -> ItemBuilder.of(XMaterial.COAL)
                    .displayName("&e" + StringHelper.titlize(modifier.name()))
                    .setLore(loreLines)
                    .build(), e -> {
                if (e.getClick() == ClickType.LEFT) {
                    lobby.addModifierYesVote(modifier, playerUUID);
                } else if (e.getClick() == ClickType.RIGHT) {
                    lobby.addModifierNoVote(modifier, playerUUID);
                }
            }, Sound.CLICK, 1.0F);
            addElement(button);
        });
    }
}