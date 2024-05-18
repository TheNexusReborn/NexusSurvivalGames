package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class MapChatOptionsMsgThread extends NexusThread<SurvivalGames> {
    
    public MapChatOptionsMsgThread(SurvivalGames plugin) {
        super(plugin, 2400L, 60L, true);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            if (lobby.getPlayers().isEmpty()) {
                continue;
            }

            if (!(lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN)) {
                continue;
            }

            for (LobbyPlayer nexusPlayer : lobby.getPlayers()) {
                lobby.sendMapOptions(nexusPlayer.getPlayer());
            }
        }
    }
}
