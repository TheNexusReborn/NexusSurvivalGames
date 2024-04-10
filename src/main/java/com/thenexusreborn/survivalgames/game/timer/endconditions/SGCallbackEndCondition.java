package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.stardevllc.starclock.condition.ClockEndCondition;
import com.stardevllc.starclock.snapshot.TimerSnapshot;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SGCallbackEndCondition implements ClockEndCondition<TimerSnapshot> {

    private Game game;
    private Set<OldGameState> validStates = new HashSet<>();
    private Consumer<Game> autoMethod, manualMethod;
    private String timerType, action;

    public SGCallbackEndCondition(Game game, Consumer<Game> autoMethod, Consumer<Game> manualMethod, String timerType, String action, OldGameState state, OldGameState... states) {
        this.game = game;
        this.autoMethod = autoMethod;
        this.manualMethod = manualMethod;
        this.timerType = timerType;
        this.action = action;
        this.validStates.add(state);
        if (states != null) {
            this.validStates.addAll(Arrays.asList(states));
        }
    }

    @Override
    public boolean shouldEnd(TimerSnapshot snapshot) {
        if (snapshot.getTime() == 0) {
            if (!validStates.contains(game.getState())) {
                return true;
            }
            if (game.getControlType() == ControlType.AUTOMATIC) {
                autoMethod.accept(game);
                return true;
            } else {
                if (manualMethod != null) {
                    manualMethod.accept(game);
                }
                game.sendMessage("&eThe " + timerType + " timer concluded but the mode is not automatic. Waiting for the command to " + action + ".");
            }
        }
        return false;
    }
}
