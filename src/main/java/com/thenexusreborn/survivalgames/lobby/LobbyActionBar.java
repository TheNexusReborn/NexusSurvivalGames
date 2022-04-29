package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.nexuscore.util.ActionBar;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.GameState;

public class LobbyActionBar extends ActionBar {
    
    private SurvivalGames plugin;
    
    public LobbyActionBar(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getText() {
        Lobby lobby = plugin.getLobby();
        if (plugin.getGame() != null) {
            return "";
        }
        
        if (lobby.getMode() == Mode.MANUAL) {
            return "&aThe lobby is currently in manual mode.";
        }
        
        if (lobby.getState() == LobbyState.WAITING || lobby.getTimer() == null) {
            return "&d&lNEXUS   &fPlaying on &f&l" + plugin.getNexusCore().getConfig().getString("serverName");
        }
        
        return "";
    }
}
