package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.timer.EpearlCooldownCallback;

import java.util.UUID;

public class EndermanMutation extends Mutation {
    
    private Timer epearlCooldownTimer;
    
    protected EndermanMutation(UUID player, UUID target) {
        super(MutationType.ENDERMAN, player, target);
    }
    
    public boolean isEpearlOnCooldown() {
        if (epearlCooldownTimer == null) {
            return false;
        }
        
        return epearlCooldownTimer.getSecondsLeft() > 0;
    }
    
    public void startEpearlCooldownTimer() {
        epearlCooldownTimer = new Timer(new EpearlCooldownCallback(this));
        epearlCooldownTimer.setLength(3000);
        epearlCooldownTimer.run();
    }
    
    public Timer getEpearlCooldownTimer() {
        return epearlCooldownTimer;
    }
    
    public void resetEpearlCooldown() {
        if (epearlCooldownTimer != null) {
            epearlCooldownTimer.cancel();
        }
        epearlCooldownTimer = null;
    }
}
