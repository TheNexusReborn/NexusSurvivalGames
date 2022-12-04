package com.thenexusreborn.survivalgames.threads;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.Lobby;

public class ServerStatusThread extends NexusThread<SurvivalGames> {
    
    public ServerStatusThread(SurvivalGames plugin) {
        super(plugin, 20L, 1L, false);
    }
    
    public void onRun() {
        Game game = plugin.getGame();
        Lobby lobby = plugin.getLobby();
        ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
        if (game != null) {
            serverInfo.setState("game:" + game.getState().toString());
        } else {
            serverInfo.setState("lobby:" + lobby.getState().toString());
        }
    }
}
