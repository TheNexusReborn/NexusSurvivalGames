package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.stardevllc.starlib.clock.condition.ClockEndCondition;
import com.stardevllc.starlib.clock.snapshot.TimerSnapshot;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.game.Game;

import java.util.*;
import java.util.function.Consumer;

public class SGCallbackEndCondition implements ClockEndCondition<TimerSnapshot> {

    private Game game;
    private Set<Game.State> validStates = new HashSet<>();
    private Consumer<Game> autoMethod, manualMethod;
    private String timerType, action;

    public SGCallbackEndCondition(Game game, Consumer<Game> autoMethod, Consumer<Game> manualMethod, String timerType, String action, Game.State state, Game.State... states) {
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
            if (game.getControlType() == ControlType.AUTO) {
                autoMethod.accept(game);
                return true;
            } else {
                if (manualMethod != null) {
                    manualMethod.accept(game);
                }
                game.sendMessage(MsgType.INFO.format("&eThe %v timer concluded but the mode is not automatic. Waiting for the command to %v.", timerType, action));
            }
        }
        return false;
    }
}
