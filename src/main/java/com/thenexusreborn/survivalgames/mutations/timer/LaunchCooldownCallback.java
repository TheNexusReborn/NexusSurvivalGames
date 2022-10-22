package com.thenexusreborn.survivalgames.mutations.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;

public class LaunchCooldownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private ChickenMutation mutation;
    
    public LaunchCooldownCallback(ChickenMutation mutation) {
        this.mutation = mutation;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (timerSnapshot.getSecondsLeft() == 0) {
            mutation.resetLaunchCooldown();
            return false;
        }
        return true;
    }
}
