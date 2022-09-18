package com.thenexusreborn.survivalgames.tasks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerStatusTask extends BukkitRunnable { 
    
    private SurvivalGames plugin;
    
    public ServerStatusTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Game game = plugin.getGame();
        Lobby lobby = plugin.getLobby();
        ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
        if (game != null) {
            serverInfo.setState("game:" + game.getState().toString());
        } else {
            serverInfo.setState("lobby:" + lobby.getState().toString());
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 1L, 20L);
    }
}
