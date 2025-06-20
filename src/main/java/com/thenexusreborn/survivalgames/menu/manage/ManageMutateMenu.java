package com.thenexusreborn.survivalgames.menu.manage;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.XMaterial;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starcore.v1_8_R1.itembuilder.SkullItemBuilder;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ManageMutateMenu extends InventoryGUI implements UpdatingGUI {

    private SurvivalGames plugin;
    private SGPlayer actor;
    private Game game;
    private MutationBuilder builder;

    public ManageMutateMenu(SurvivalGames plugin, SGPlayer actor, Game game, MutationBuilder builder) {
        super(1, "Mutate " + builder.getPlayer().getName() + " as...", actor.getUniqueId());
        this.plugin = plugin;
        this.builder = builder;
        this.game = game;
        this.actor = actor;
    }

    @Override
    public void createItems() {
        GuiManager manager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        IMutationType type = builder.getType();
        Button typeButton = new Button().iconCreator(p -> ItemBuilder.of(type.getIcon()).displayName("&a&lTYPE").addLoreLine("&e&l" + type.getDisplayName()).build())
                .consumer(e -> manager.openGUI(new ManageMutateSelectMenu(plugin, actor, builder, this), e.getWhoClicked()));
        addElement(typeButton);

        Button targetButton = new Button().iconCreator(p -> {
                    if (builder.getTarget() == null) {
                        return new ItemStack(Material.BARRIER);
                    }

                    GamePlayer targetPlayer = builder.getTarget();
                    
                    SkullItemBuilder skullItemBuilder = new SkullItemBuilder()
                            .owner(targetPlayer.getName())
                            .displayName("&e&lTARGET")
                            .addLoreLine(targetPlayer.getDisplayName());;

                    return skullItemBuilder.build();
                })
                .consumer(e -> manager.openGUI(new ManageMutateTargetMenu(plugin, actor, game, builder, this), e.getWhoClicked()));
        addElement(targetButton);

        Button bypassTimer = new Button().iconCreator(p -> 
                ItemBuilder.of(builder.isBypassTimer() ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                        .displayName("&e&lBYPASS TIMER")
                        .setLore(List.of("", "&2&lGREEN WOOL &fmeans true", "&c&lRED WOOL &fmeans false", "", "&cfalse &fis the default"))
                        .build())
                .consumer(e -> builder.setBypassTimer(!builder.isBypassTimer()));
        addElement(bypassTimer);
        
        Button cancelButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.REDSTONE_BLOCK).displayName("&4&lCANCEL").build())
                .consumer(e -> manager.openGUI(new PlayerManageMenu(plugin, game, new PlayerManageBuilder(actor, builder.getPlayer())), e.getWhoClicked()));
        setElement(7, cancelButton);

        Button confirmButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.EMERALD_BLOCK).displayName("&a&lCONFIRM").build())
                .consumer(e -> {
                    if (builder.getTarget() == null) {
                        actor.sendMessage(MsgType.WARN.format("The target is invalid"));
                        return;
                    }
                    game.mutatePlayer(actor, builder);
                    e.getWhoClicked().closeInventory();
                });
        setElement(8, confirmButton);
    }
}
