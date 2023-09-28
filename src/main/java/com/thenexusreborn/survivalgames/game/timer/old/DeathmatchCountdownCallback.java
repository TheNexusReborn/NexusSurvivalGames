package com.thenexusreborn.survivalgames.game.timer.old;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.*;

public class DeathmatchCountdownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    
    private final Game game;
    private final Set<Integer> announced = new HashSet<>();
    
    public DeathmatchCountdownCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        if (remainingSeconds <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.startDeathmatch();
            } else {
                game.deathmatchWarmupDone();
                game.sendMessage("&eThe timer concluded but the mode is not automatic. Waiting for the command to start the deathmatch.");
            }
            return false;
        }
        
        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.ENDERDRAGON_HIT);
                }
                this.announced.add(remainingSeconds);
            }
        }
        return true;
    }
}
