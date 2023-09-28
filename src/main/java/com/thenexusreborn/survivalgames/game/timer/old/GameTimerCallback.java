package com.thenexusreborn.survivalgames.game.timer.old;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Sound;

import java.util.*;

public class GameTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> DEATHMATCH_START_COUNTDOWN_ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    private static final Set<Integer> GAME_ANNOUNCE_MINUTES = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2)); //1 is handled by the other announce above
    
    private final Set<Integer> announcedSeconds = new HashSet<>();
    private final Set<Integer> announcedMinutes = new HashSet<>();
    private Set<Integer> chestsRestocked = new HashSet<>();
    
    private boolean announcedRatingMessage;
    
    private final Game game;
    
    public GameTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (!(game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME)) {
            return false;
        }
        
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        int remainingMinutes = (int) Math.ceil(remainingSeconds / 60D);
        
        if (GAME_ANNOUNCE_MINUTES.contains(remainingMinutes)) {
            if (!this.announcedMinutes.contains(remainingMinutes)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announcedMinutes.add(remainingMinutes);
            }
        }
        
        if (DEATHMATCH_START_COUNTDOWN_ANNOUNCE.contains(remainingSeconds)) {
            if (!this.announcedSeconds.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announcedSeconds.add(remainingSeconds);
            }
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.teleportDeathmatch();
            } else {
                game.sendMessage("&eThe game timer has concluded, but the mode is in manual, not starting deathmatch automatically.");
            }
            return false;
        }
    
        return true;
    }
}
