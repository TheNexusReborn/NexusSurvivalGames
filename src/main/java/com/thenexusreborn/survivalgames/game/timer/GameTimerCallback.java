package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.Mode;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Sound;

import java.util.*;

public class GameTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> DEATHMATCH_START_COUNTDOWN_ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    private static final Set<Integer> GAME_ANNOUNCE_MINUTES = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2)); //1 is handled by the other announce above
    
    private Set<Integer> announcedSeconds = new HashSet<>(), announcedMinutes = new HashSet<>(), chestsRestocked = new HashSet<>();
    
    private Game game;
    
    public GameTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (!(game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME)) {
            return false; //The deathmatch being triggered by something else is a separate timer. 
        }
        
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        int remainingMinutes = (int) Math.ceil(remainingSeconds / 60D);
        
        boolean restockChests = (remainingMinutes % 10) == 0;
        
        if (GAME_ANNOUNCE_MINUTES.contains(remainingMinutes)) {
            if (!this.announcedMinutes.contains(remainingMinutes)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Timer.formatTime(remainingSeconds) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announcedMinutes.add(remainingMinutes);
            }
        }
        
        if (DEATHMATCH_START_COUNTDOWN_ANNOUNCE.contains(remainingSeconds)) {
            if (!this.announcedSeconds.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + Timer.formatTime(remainingSeconds) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announcedSeconds.add(remainingSeconds);
            }
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getMode() == Mode.AUTOMATIC) {
                game.teleportDeathmatch();
            } else {
                game.sendMessage("&eThe game timer has concluded, but the mode is in manual, not starting deathmatch automatically.");
            }
            return false;
        }
    
        return true;
    }
}
