package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;

public class LobbyActionBar implements IActionBar {
    
    private final SurvivalGames plugin;
    
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
            return "&f&lVoting closes in &e&l" + Game.SHORT_TIME_FORMAT.format(lobby.getTimer().getTime());
        }
        
        return "";
    }
}
