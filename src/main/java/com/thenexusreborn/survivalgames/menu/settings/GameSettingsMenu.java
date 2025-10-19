package com.thenexusreborn.survivalgames.menu.settings;

import com.stardevllc.itembuilder.ItemBuilders;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starcore.api.ui.element.button.Button;
import com.stardevllc.starcore.api.ui.gui.InventoryGUI;
import com.stardevllc.starcore.api.ui.gui.UpdatingGUI;
import com.thenexusreborn.survivalgames.settings.GameSettings;

import java.util.UUID;

public class GameSettingsMenu extends InventoryGUI implements UpdatingGUI {
    
    private GameSettings settings;
    
    public GameSettingsMenu(UUID player, GameSettings settings) {
        super("Game Settings", player, new String[]{"SCBATMPLO", "---WGD---"});
        this.settings = settings;
    }
    
    @Override
    public void createItems() {
        //TODO Formatting and functionality
        Button sponsoringButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.CHEST).build());
        setElement('S', sponsoringButton);
        
        Button combatTagButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.IRON_SWORD).build());
        setElement('C', combatTagButton);
        
        Button bountiesButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.EMERALD).build());
        setElement('B', bountiesButton);
        
        Button assistsButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.WOODEN_SWORD).build());
        setElement('A', assistsButton);
        
        Button statsButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.IRON_INGOT).build());
        setElement('T', statsButton);
        
        Button mutationsButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.ROTTEN_FLESH).build());
        setElement('M', mutationsButton);
        
        Button playerButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.PLAYER_HEAD).build());
        setElement('P', playerButton);
        
        Button lootButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.GOLDEN_APPLE).build());
        setElement('L', lootButton);
        
        Button cosmeticsButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.QUARTZ_BLOCK).build());
        setElement('O', cosmeticsButton);
        
        Button worldButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.COBBLESTONE).build());
        setElement('W', worldButton);
        
        Button gameButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.REDSTONE).build());
        setElement('G', gameButton);
        
        Button deathmatchButton = new Button().iconCreator(p -> ItemBuilders.of(SMaterial.NETHER_STAR).build());
        setElement('D', deathmatchButton);
    }
}
