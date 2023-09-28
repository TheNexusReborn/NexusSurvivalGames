package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import me.firestar311.starclock.api.condition.ClockEndCondition;
import me.firestar311.starclock.api.snapshot.TimerSnapshot;

public class WarmupEndCondition implements ClockEndCondition<TimerSnapshot> {
    
    private Game game;

    public WarmupEndCondition(Game game) {
        this.game = game;
    }

    @Override
    public boolean shouldEnd(TimerSnapshot snapshot) {
        if (snapshot.getTime() == 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                if (game.getState() == GameState.WARMUP || game.getState() == GameState.WARMUP_DONE) {
                    game.startGame();
                    return true;
                }
            } else {
                game.warmupComplete();
                game.sendMessage("&eThe warmup timer concluded but the mode is not automatic. Waiting for the command to start game.");
            }
        }
        return false;
    }
}
