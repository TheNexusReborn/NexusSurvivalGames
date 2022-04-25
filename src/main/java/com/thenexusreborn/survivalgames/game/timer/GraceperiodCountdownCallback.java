package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.Mode;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.*;

public class GraceperiodCountdownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 3, 2, 1));
    
    private Game game;
    private Set<Integer> announced = new HashSet<>();
    
    public GraceperiodCountdownCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        if (remainingSeconds <= 0) {
            if (Game.getMode() == Mode.AUTOMATIC) {
                game.endGracePeriod();
            } 
//            else {
//                game.gracePeriodComplete();
//                game.sendMessage("&eThe graceperiod timer concluded but the mode is not automatic. Waiting for the command to start game.");
//            }
            return false;
        }
        
        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe &c&lGRACE PERIOD &eends in &b" + Timer.formatTime(remainingSeconds) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announced.add(remainingSeconds);
            }
        }
        return true;
    }
}
