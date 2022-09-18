package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteStartMsgTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public VoteStartMsgTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        if (plugin.getGame() != null) {
            return;
        }
    
        Lobby lobby = plugin.getLobby();
        if (lobby == null) {
            return;
        }
    
        if (lobby.getPlayers().size() == 0) {
            return;
        }
    
        if (lobby.getState() != LobbyState.WAITING) {
            return;
        }
    
        if (lobby.getPlayers().size() < lobby.getLobbySettings().getMinPlayers()) {
            lobby.sendMessage("&6&l>> &e&lDid you know that you can use &f&l/votestart &e&lto start a game early?");
        }
    }
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 20L, 2400L);
    }
}
