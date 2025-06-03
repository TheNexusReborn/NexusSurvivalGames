package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starcore.api.XMaterial;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.survivalgames.settings.GameSettings;

import java.util.UUID;

public class GameSettingsMenu extends InventoryGUI implements UpdatingGUI {
    
    private GameSettings settings;
    
    public GameSettingsMenu(UUID player, GameSettings settings) {
        super(6, "Game Settings", player);
        this.settings = settings;
    }
    
    @Override
    public void createItems() {
        //TODO Formatting and functionality
        Button sponsoringButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.CHEST).build());
        
        Button combatTagButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.IRON_SWORD).build());
        
        Button bountiesButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.EMERALD).build());
        
        Button assistsButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.WOODEN_SWORD).build());
        
        Button statsButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.IRON_INGOT).build());
        
        Button mutationsButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.ROTTEN_FLESH).build());
        
        Button playerButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.PLAYER_HEAD).build());
        
        Button lootButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.GOLDEN_APPLE).build());
        
        Button cosmeticsButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.QUARTZ_BLOCK).build());
        
        Button worldButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.COBBLESTONE).build());
        
        Button gameButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.REDSTONE).build());
        
        Button deathmatchButton = new Button().iconCreator(p -> ItemBuilder.of(XMaterial.NETHER_STAR).build());
        
        setElement(0, sponsoringButton);
        setElement(1, combatTagButton);
        setElement(2, bountiesButton);
        setElement(3, assistsButton);
        setElement(4, statsButton);
        setElement(5, mutationsButton);
        setElement(6, playerButton);
        setElement(7, lootButton);
        setElement(8, cosmeticsButton);
        setElement(9, worldButton);
        setElement(10, gameButton);
        setElement(11, deathmatchButton);
    }
}
