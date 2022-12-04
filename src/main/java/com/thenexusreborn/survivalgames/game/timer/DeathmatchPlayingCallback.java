package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Sound;

import java.util.*;

public class DeathmatchPlayingCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    
    private final Game game;
    private final Set<Integer> announced = new HashSet<>();
    
    public DeathmatchPlayingCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game.getState() != GameState.INGAME_DEATHMATCH) {
            return false;
        }
        
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        if (ANNOUNCE.contains(remainingSeconds)) {
            if (!announced.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announced.add(remainingSeconds);
            }
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.teleportDeathmatch();
            } else {
                game.sendMessage("&eThe deathmatch countdown timer has concluded, but the mode is set to manual. Automatic deathmatch setup has been skipped.");
            }
            return false;
        }
        
        return true;
    }
}
