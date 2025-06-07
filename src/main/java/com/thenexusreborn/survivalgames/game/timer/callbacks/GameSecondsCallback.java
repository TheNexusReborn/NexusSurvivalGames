package com.thenexusreborn.survivalgames.game.timer.callbacks;

import com.stardevllc.clock.callback.CallbackPeriod;
import com.stardevllc.clock.callback.ClockCallback;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class GameSecondsCallback implements ClockCallback<TimerSnapshot> {
    private static final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    
    private Game game;
    private Supplier<String> msgSupplier;
    private boolean announceMinute;
    private Sound sound;

    public GameSecondsCallback(Game game, Sound sound, String message) {
        this(game, sound, () -> message);
    }

    public GameSecondsCallback(Game game, Sound sound, String message, boolean announceMinute) {
        this(game, sound, () -> message, announceMinute);
    }

    public GameSecondsCallback(Game game, Sound sound, Supplier<String> msgSupplier) {
        this(game, sound, msgSupplier, false);
    }

    public GameSecondsCallback(Game game, Sound sound, Supplier<String> msgSupplier, boolean announceMinute) {
        this.game = game;
        this.msgSupplier = msgSupplier;
        this.announceMinute = announceMinute;
        this.sound = sound;
    }
    
    @Override
    public void callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(timerSnapshot.getTime());
        if (ANNOUNCE.contains(remainingSeconds)) {
            
            if (remainingSeconds == 60 && !announceMinute) {
                return;
            }
            
            game.sendMessage(msgSupplier.get().replace("{time}", Game.LONG_TIME_FORMAT.format(timerSnapshot.getTime())));
            if (game.getSettings().isSounds()) {
                game.playSound(sound);
            }
        }
    }

    @Override
    public CallbackPeriod getPeriod() {
        return () -> 1000L;
    }
}
