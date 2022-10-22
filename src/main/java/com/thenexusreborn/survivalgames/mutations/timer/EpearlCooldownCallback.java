package com.thenexusreborn.survivalgames.mutations.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.mutations.impl.EndermanMutation;

public class EpearlCooldownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private EndermanMutation mutation;
    
    public EpearlCooldownCallback(EndermanMutation mutation) {
        this.mutation = mutation;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (timerSnapshot.getSecondsLeft() == 0) {
            mutation.resetEpearlCooldown();
            return false;
        }
        return true;
    }
}
