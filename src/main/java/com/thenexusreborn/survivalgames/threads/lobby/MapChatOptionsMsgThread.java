package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;

public class MapChatOptionsMsgThread extends NexusThread<SurvivalGames> {
    
    public MapChatOptionsMsgThread(SurvivalGames plugin) {
        super(plugin, 2400L, 60L, true);
    }
    
    public void onRun() {
        Lobby lobby = plugin.getLobby();
        
        if (plugin.getGame() != null) {
            return;
        }
    
        if (lobby.getPlayers().isEmpty()) {
            return;
        }
    
        if (!(lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN)) {
            return;
        }
    
        for (LobbyPlayer nexusPlayer : lobby.getPlayers()) {
            lobby.sendMapOptions(nexusPlayer.getPlayer());
        }
    }
}
