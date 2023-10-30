package com.thenexusreborn.survivalgames.game.timer.old;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;

import java.util.*;

public class NextGameTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    private static final Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));

    private final Game game;
    private final Set<Integer> announced = new HashSet<>();

    public NextGameTimerCallback(Game game) {
        this.game = game;
    }

    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game.getState() != GameState.ENDING) {
            return false;
        }

        if (game.getPlayers().isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
            game.nextGame();
            return false;
        }

        int remainingSeconds = timerSnapshot.getSecondsLeft();

        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eNext game starts in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                this.announced.add(remainingSeconds);
            }
        }

        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.nextGame();
            } else {
                game.sendMessage("&eThe next game timer has concluded, but the mode is not automatic. Skipped automatically performing next game tasks.");
            }
            return false;
        }

        return true;
    }
}
