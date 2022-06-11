package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerCountdownCheck extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public TimerCountdownCheck(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
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
        
        int playerCount = 0;
        for (SpigotNexusPlayer player : lobby.getPlayers()) {
            if (!lobby.getSpectatingPlayers().contains(player.getUniqueId())) {
                if (!player.getPreferences().get("vanish").getValue()) {
                    playerCount++;
                }
            }
        }
        
        if (playerCount >= lobby.getLobbySettings().getMinPlayers()) {
            lobby.startTimer();
            lobby.sendMessage("&eMinimum player count has been met, starting countdown to game start.");
        }
    }
}
