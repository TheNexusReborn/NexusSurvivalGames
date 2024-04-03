package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.starclock.clocks.Timer;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class LobbyActionBar implements IActionBar {
    
    private final SurvivalGames plugin;
    private final SGPlayer player;

    public LobbyActionBar(SurvivalGames plugin, SGPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    @Override
    public String getText() {
        Lobby lobby = player.getLobby();
        if (player.getLobby() == null) {
            return null;
        }
        
        if (lobby.getControlType() == ControlType.MANUAL) {
            return "&aThe lobby is currently in manual mode.";
        }

        Timer timer = lobby.getTimer();
        if (lobby.getState() == LobbyState.WAITING || timer == null) {
            return "&d&lNEXUS &7- &fPlaying on &f&l" + lobby.getServer().getName();
        }
        
        if (lobby.getState() == LobbyState.COUNTDOWN) {
            int remainingSeconds = (int) TimeUnit.SECONDS.fromMillis(timer.getTime()) + 1;
            return "&f&lVoting closes in &e&l" + remainingSeconds + "s"/*Game.SHORT_TIME_FORMAT.format(TimeUnit.SECONDS.toMilliseconds(remainingSeconds)) */;
        }
        
        return "";
    }
}
