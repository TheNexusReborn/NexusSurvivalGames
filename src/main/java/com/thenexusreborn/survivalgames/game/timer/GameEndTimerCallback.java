package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Sound;

import java.util.*;

public class GameEndTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> SECONDS_ANNOUNCE = new HashSet<>(Arrays.asList(45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    private static final Set<Integer> MINUTES_ANNOUCNE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1));
    
    private Set<Integer> announcedSeconds = new HashSet<>(), announcedMinutes = new HashSet<>();
    
    private Game game;
    
    public GameEndTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game.getState() != GameState.DEATHMATCH) {
            return false;
        }
        
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        int remainingMinutes = (int) Math.ceil((remainingSeconds / 60.0));
        
        if (MINUTES_ANNOUCNE.contains(remainingMinutes)) {
            if (!announcedMinutes.contains(remainingMinutes)) {
                game.sendMessage("&6&l>> &eThe game &c&lENDS &ein &b" + Timer.formatTime(remainingSeconds) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                announcedMinutes.add(remainingMinutes);
            }
        }
        
        if (SECONDS_ANNOUNCE.contains(remainingSeconds)) {
            if (!announcedSeconds.contains(remainingSeconds)) {
                game.sendMessage("&eThe &c&lGAME &eends in &b" + Timer.formatTime(remainingSeconds) + ".");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announcedSeconds.add(remainingSeconds);
            }
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.end();
            } else {
                game.sendMessage("&eThe game end timer has concluded, but the mode is not automatic. Skipped automatically performing end of game tasks.");
            }
            return false;
        }
        
        return true;
    }
}
