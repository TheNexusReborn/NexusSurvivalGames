package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.itembuilder.ItemBuilders;
import com.stardevllc.starcore.api.ui.GuiManager;
import com.stardevllc.starcore.api.ui.element.button.Button;
import com.stardevllc.starcore.api.ui.gui.InventoryGUI;
import com.stardevllc.starcore.api.ui.gui.UpdatingGUI;
import com.stardevllc.starmclib.XMaterial;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.IMutationType;
import com.thenexusreborn.survivalgames.mutations.MutationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ManageMutateMenu extends InventoryGUI implements UpdatingGUI {
    public ManageMutateMenu(SurvivalGames plugin, SGPlayer actor, Game game, MutationBuilder builder) {
        super("Mutate " + builder.getPlayer().getName() + " as...", actor.getUniqueId(), new String[]{"TPB----CG"});
        
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        IMutationType type = builder.getType();
        Button typeButton = new Button().iconCreator(p -> ItemBuilders.of(type.getIcon()).displayName("&a&lTYPE").addLoreLine("&e&l" + type.getDisplayName()).build())
                .consumer(e -> manager.openGUI(new ManageMutateSelectMenu(plugin, actor, builder, this), e.getWhoClicked()));
        setElement('T', typeButton);
        
        Button targetButton = new Button().iconCreator(p -> {
                    if (builder.getTarget() == null) {
                        return new ItemStack(Material.BARRIER);
                    }
                    
                    GamePlayer targetPlayer = builder.getTarget();
                    
                    ItemStack skull = SpigotUtils.getPlayerSkull(Bukkit.getPlayer(targetPlayer.getUniqueId()));
                    ItemMeta meta = skull.getItemMeta();
                    meta.setDisplayName(StarColors.color("&e&lTARGET"));
                    meta.setLore(List.of(StarColors.color(targetPlayer.getDisplayName())));
                    skull.setItemMeta(meta);
                    return skull;
                })
                .consumer(e -> manager.openGUI(new ManageMutateTargetMenu(plugin, actor, game, builder, this), e.getWhoClicked()));
        setElement('P', targetButton);
        
        Button bypassTimer = new Button().iconCreator(p ->
                        ItemBuilders.of(builder.isBypassTimer() ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                                .displayName("&e&lBYPASS TIMER")
                                .setLore(List.of("", "&2&lGREEN WOOL &fmeans true", "&c&lRED WOOL &fmeans false", "", "&cfalse &fis the default"))
                                .build())
                .consumer(e -> builder.setBypassTimer(!builder.isBypassTimer()));
        addElement(bypassTimer);
        
        Button cancelButton = new Button().iconCreator(p -> ItemBuilders.of(XMaterial.REDSTONE_BLOCK).displayName("&4&lCANCEL").build())
                .consumer(e -> manager.openGUI(new PlayerManageMenu(plugin, game, new PlayerManageBuilder(actor, builder.getPlayer())), e.getWhoClicked()));
        setElement('C', cancelButton);
        
        Button confirmButton = new Button().iconCreator(p -> ItemBuilders.of(XMaterial.EMERALD_BLOCK).displayName("&a&lCONFIRM").build())
                .consumer(e -> {
                    if (builder.getTarget() == null) {
                        actor.sendMessage(MsgType.WARN.format("The target is invalid"));
                        return;
                    }
                    game.mutatePlayer(actor, builder);
                    e.getWhoClicked().closeInventory();
                });
        setElement('G', confirmButton);
    }
}