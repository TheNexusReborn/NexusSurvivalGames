package com.thenexusreborn.survivalgames.threads.lobby;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class MapChatOptionsMsgThread extends StarThread<SurvivalGames> {
    
    public MapChatOptionsMsgThread(SurvivalGames plugin) {
        super(plugin, 2400L, 60L, true);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers().values()) {
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
