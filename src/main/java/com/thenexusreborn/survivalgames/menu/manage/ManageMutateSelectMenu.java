package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.starcore.api.ui.GuiManager;
import com.stardevllc.starcore.api.ui.element.button.Button;
import com.stardevllc.starcore.api.ui.gui.InventoryGUI;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Bukkit;

public class ManageMutateSelectMenu extends InventoryGUI {
    public ManageMutateSelectMenu(SurvivalGames plugin, SGPlayer actor, MutationBuilder builder, ManageMutateMenu previousMenu) {
        super("&lMutate " + builder.getPlayer().getName() + " as...", actor.getUniqueId(), new String[] {"MMMMMMMMM"});
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        setDynamicChar('M');
        for (IMutationType type : StandardMutations.values()) {
            if (plugin.getDisabledMutations().contains(type)) {
                continue;
            }
            
            Button button = new Button().iconCreator(p -> ItemBuilders.of(type.getIcon()).displayName("&e&l" + type.getDisplayName()).build())
                    .consumer(e -> {
                        builder.setType(type);
                        manager.openGUI(previousMenu, e.getWhoClicked());
                    });
            addElement(button);
        }
    }
}
