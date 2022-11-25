package com.thenexusreborn.survivalgames.tasks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.Lobby;

public class ServerStatusTask extends NexusTask<SurvivalGames> {
    
    public ServerStatusTask(SurvivalGames plugin) {
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
