package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.stardevllc.clock.callback.ClockCallback;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameSecondsCallback implements ClockCallback<TimerSnapshot> {
    private static final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    
    private Game game;
    private String message;
    private boolean announceMinute;
    
    public GameSecondsCallback(Game game, String message) {
        this(game, message, true);
    }
    
    public GameSecondsCallback(Game game, String message, boolean announceMinute) {
        this.game = game;
        this.message = message;
        this.announceMinute = announceMinute;
    }

    @Override
    public void callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(timerSnapshot.getTime());
        if (ANNOUNCE.contains(remainingSeconds)) {
            
            if (remainingSeconds == 60 && !announceMinute) {
                return;
            }
            
            game.sendMessage(message.replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
            if (game.getSettings().isSounds()) {
                game.playSound(Sound.CLICK);
            }
        }
    }

    @Override
    public long getPeriod() {
        return 1000L;
    }
}
