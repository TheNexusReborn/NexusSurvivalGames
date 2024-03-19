package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.stardevllc.starclock.callback.ClockCallback;
import com.stardevllc.starclock.snapshot.TimerSnapshot;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameSecondsCallback implements ClockCallback<TimerSnapshot> {
    private static final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    
    private Game game;
    private String message;
    
    public GameSecondsCallback(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    @Override
    public void callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = (int) Math.ceil(timerSnapshot.getTime() * 1.0 / getPeriod());
        if (ANNOUNCE.contains(remainingSeconds)) {
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
