package com.thenexusreborn.survivalgames.mutations.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;

public class ParachuteCooldownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private ChickenMutation mutation;
    
    public ParachuteCooldownCallback(ChickenMutation mutation) {
        this.mutation = mutation;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (timerSnapshot.getSecondsLeft() == 0) {
            mutation.resetParachuteCooldown();
            return false;
        }
        return true;
    }
}
