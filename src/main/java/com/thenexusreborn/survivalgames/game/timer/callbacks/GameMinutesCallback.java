package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.thenexusreborn.survivalgames.game.Game;
import me.firestar311.starclock.api.callback.ClockCallback;
import me.firestar311.starclock.api.snapshot.TimerSnapshot;
import org.bukkit.Sound;

public class GameMinutesCallback implements ClockCallback<TimerSnapshot> {
    private Game game;
    private String message;

    public GameMinutesCallback(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    public boolean callback(TimerSnapshot timerSnapshot) {
        int remainingMinutes = (int) Math.ceil(timerSnapshot.getTime() * 1.0 / getInterval());
        game.sendMessage(message.replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
        if (game.getSettings().isSounds()) {
            game.playSound(Sound.CLICK);
        }

        return true;
    }

    @Override
    public long getInterval() {
        return 60000L;
    }
}
