package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Bukkit;

public class ManageMutateSelectMenu extends InventoryGUI {
    public ManageMutateSelectMenu(SurvivalGames plugin, SGPlayer actor, MutationBuilder builder, ManageMutateMenu previousMenu) {
        super(1, "&lMutate " + builder.getPlayer().getName() + " as...", actor.getUniqueId());
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        for (IMutationType type : StandardMutations.values()) {
            if (plugin.getDisabledMutations().contains(type)) {
                continue;
            }
            
            Button button = new Button().iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&e&l" + type.getDisplayName()).build())
                    .consumer(e -> {
                        builder.setType(type);
                        manager.openGUI(previousMenu, e.getWhoClicked());
                    });
            addElement(button);
        }
    }
}
