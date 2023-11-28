package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.starclock.clocks.Timer;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class LobbyActionBar implements IActionBar {
    
    private final SurvivalGames plugin;
    
    public LobbyActionBar(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getText() {
        Lobby lobby = plugin.getLobby();
        if (plugin.getGame() != null) {
            return "";
        }
        
        if (lobby.getControlType() == ControlType.MANUAL) {
            return "&aThe lobby is currently in manual mode.";
        }

        Timer timer = lobby.getTimer();
        if (lobby.getState() == LobbyState.WAITING || timer == null) {
            return "&d&lNEXUS &7- &fPlaying on &f&l" + NexusAPI.getApi().getServerManager().getCurrentServer().getName();
        }
        
        if (lobby.getState() == LobbyState.COUNTDOWN) {
            int remainingSeconds = (int) Math.ceil(timer.getTime() / 1000.0);
            return "&f&lVoting closes in &e&l" + remainingSeconds + "s"/*Game.SHORT_TIME_FORMAT.format(TimeUnit.SECONDS.toMilliseconds(remainingSeconds)) */;
        }
        
        return "";
    }
}
