package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.stardevllc.clock.callback.ClockCallback;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.function.Supplier;

public class GameMinutesCallback implements ClockCallback<TimerSnapshot> {
    private Game game;
    private Supplier<String> msgSupplier;

    public GameMinutesCallback(Game game, String message) {
        this(game, () -> message);
    }

    public GameMinutesCallback(Game game, Supplier<String> msgSupplier) {
        this.game = game;
        this.msgSupplier = msgSupplier;
    }

    public void callback(TimerSnapshot timerSnapshot) {
        game.sendMessage(msgSupplier.get().replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
        if (game.getSettings().isSounds()) {
            game.playSound(Sound.CLICK);
        }
    }

    @Override
    public long getPeriod() {
        return 60000L;
    }
}
