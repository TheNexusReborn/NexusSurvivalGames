package com.thenexusreborn.survivalgames.game.timer.endconditions;

import com.stardevllc.starclock.snapshot.TimerSnapshot;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import com.thenexusreborn.survivalgames.game.state.phase.WarmupPhase;

public class WarmupEndCondition extends SGCallbackEndCondition {
    
    private WarmupPhase phase;
    
    public WarmupEndCondition(WarmupPhase phase) {
        super(phase.getGame(), Game::startGame, Game::warmupComplete, "warmup", "start the game", GameState.WARMUP, GameState.WARMUP_DONE);
    }

    @Override
    public boolean shouldEnd(TimerSnapshot snapshot) {
        phase.setStatus(PhaseStatus.COMPLETE);
        return super.shouldEnd(snapshot);
    }
}
