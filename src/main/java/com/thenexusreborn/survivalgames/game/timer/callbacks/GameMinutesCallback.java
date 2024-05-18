package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.stardevllc.starlib.clock.callback.ClockCallback;
import com.stardevllc.starlib.clock.snapshot.TimerSnapshot;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

public class GameMinutesCallback implements ClockCallback<TimerSnapshot> {
    private Game game;
    private String message;

    public GameMinutesCallback(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    public void callback(TimerSnapshot timerSnapshot) {
        game.sendMessage(message.replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
        if (game.getSettings().isSounds()) {
            game.playSound(Sound.CLICK);
        }
    }

    @Override
    public long getPeriod() {
        return 60000L;
    }
}
