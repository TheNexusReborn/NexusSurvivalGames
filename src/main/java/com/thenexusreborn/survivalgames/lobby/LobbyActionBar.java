package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.nexuscore.util.ActionBar;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;

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
        
        if (lobby.getControlType() == ControlType.MANUAL) {
            return "&aThe lobby is currently in manual mode.";
        }
        
        if (lobby.getState() == LobbyState.WAITING || lobby.getTimer() == null) {
            return "&d&lNEXUS &7- &fPlaying on &f&l" + NexusAPI.getApi().getServerManager().getCurrentServer().getName();
        }
        
        if (lobby.getState() == LobbyState.COUNTDOWN && lobby.getTimer() != null) {
            return "&f&lVoting closes in &e&l" + Timer.formatTimeShort(lobby.getTimer().getSecondsLeft());
        }
        
        return "";
    }
}
