package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.scheduler.BukkitRunnable;

public class MapChatOptionsMsgTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public MapChatOptionsMsgTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
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
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 60L, 2400);
    }
}
