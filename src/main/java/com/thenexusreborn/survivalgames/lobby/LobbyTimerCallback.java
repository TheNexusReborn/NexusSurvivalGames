package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.Mode;
import org.bukkit.Sound;

import java.util.*;

public class LobbyTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 3, 2, 1));
    
    private Lobby lobby;
    private Set<Integer> announced = new HashSet<>();
    
    public LobbyTimerCallback(Lobby lobby) {
        this.lobby = lobby;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (lobby.getState() != LobbyState.COUNTDOWN) {
            return false;
        }
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        if (remainingSeconds <= 0) {
            if (lobby.getMode() == Mode.AUTOMATIC) {
                lobby.prepareGame();
            } else {
                lobby.sendMessage("&eThe timer concluded but the mode is not automatic, skipping auto-start of the game.");
            }
            return false;
        }
        
        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                lobby.sendMessage("&6&l>> &e&lVoting closes in &f&l" + remainingSeconds + "&f&ls&e&l.");
                if (lobby.getLobbySettings().isSounds()) {
                    lobby.playSound(Sound.CLICK);
                }
                this.announced.add(remainingSeconds);
            }
        }
        
        return true;
    }
}
