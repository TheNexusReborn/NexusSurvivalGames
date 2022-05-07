package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;

import java.util.*;

public class NextGameTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    private static Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    
    private Game game;
    private Set<Integer> announced = new HashSet<>();
    
    public NextGameTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game.getState() != GameState.ENDING) {
            return false;
        }
    
        int remainingSeconds = timerSnapshot.getSecondsLeft();
    
        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                if (!SurvivalGames.getPlugin(SurvivalGames.class).restart()) {
                    game.sendMessage("&6&l>> &eNext game starts in &b" + Timer.formatLongerTime(remainingSeconds) + "&e.");
                } else {
                    game.sendMessage("&6&l>> &eServer restarting in &b" + Timer.formatLongerTime(remainingSeconds));
                }
                this.announced.add(remainingSeconds);
            }
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.nextGame();
            } else {
                game.sendMessage("&eThe next game timer has concluded, but the mode is not automatic. Skipped automatically performing next game tasks.");
            }
            return false;
        }
        
        return true;
    }
}
