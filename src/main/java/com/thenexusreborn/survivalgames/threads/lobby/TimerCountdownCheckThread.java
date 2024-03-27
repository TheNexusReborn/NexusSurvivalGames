package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;

public class TimerCountdownCheckThread extends NexusThread<SurvivalGames> {
    
    public TimerCountdownCheckThread(SurvivalGames plugin) {
        super(plugin, 1L, false);
    }
    
    @Override
    public void onRun() {
        Lobby lobby = plugin.getLobby();
        
        if (lobby.getState() != LobbyState.WAITING) {
            return;
        }
        
        if (lobby.getControlType() == ControlType.MANUAL) {
            return;
        }
        
        if (lobby.getTimer() != null) {
            return;
        }
        
        int playerCount = lobby.getPlayingCount();
        
        if (playerCount >= lobby.getLobbySettings().getMinPlayers()) {
            lobby.startTimer();
            lobby.sendMessage("&eMinimum player count has been met, starting countdown to game start.");
        }
    }
}
