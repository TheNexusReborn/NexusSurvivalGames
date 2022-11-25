package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;

public class MapChatOptionsMsgTask extends NexusTask<SurvivalGames> {
    
    public MapChatOptionsMsgTask(SurvivalGames plugin) {
        super(plugin, 2400L, 60L, true);
    }
    
    public void onRun() {
        Lobby lobby = plugin.getLobby();
        
        if (plugin.getGame() != null) {
            return;
        }
    
        if (lobby.getPlayers().size() == 0) {
            return;
        }
    
        if (!(lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN)) {
            return;
        }
    
        for (NexusPlayer nexusPlayer : lobby.getPlayers()) {
            lobby.sendMapOptions(nexusPlayer);
        }
    }
}
