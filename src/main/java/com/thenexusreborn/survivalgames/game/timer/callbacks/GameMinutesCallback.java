package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.thenexusreborn.survivalgames.game.Game;
import me.firestar311.starclock.api.callback.ClockCallback;
import me.firestar311.starclock.api.snapshot.TimerSnapshot;
import me.firestar311.starlib.api.time.TimeUnit;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameMinutesCallback implements ClockCallback<TimerSnapshot> {
    private static final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2));

    private Game game;
    private String message;

    public GameMinutesCallback(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    public boolean callback(TimerSnapshot timerSnapshot) {
        int remainingMinutes = (int) TimeUnit.MINUTES.fromMillis(timerSnapshot.getTime());
        if (ANNOUNCE.contains(remainingMinutes)) {
            game.sendMessage(message.replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
            if (game.getSettings().isSounds()) {
                game.playSound(Sound.CLICK);
            }
        }

        return true;
    }

    @Override
    public long getInterval() {
        return 60000L;
    }
}
