package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.timer.*;

import java.util.UUID;

public class ChickenMutation extends Mutation {
    
    private Timer launchCooldownTimer;
    private Timer parachuteCooldownTimer;
    
    private int ammunition;
    
    protected ChickenMutation(UUID player, UUID target) {
        super(MutationType.CHICKEN, player, target);
    }
    
    public boolean isLaunchOnCooldown() {
        if (launchCooldownTimer == null) {
            return false;
        }
        
        return launchCooldownTimer.getSecondsLeft() > 0;
    }
    
    public boolean isParachuteOnCooldown() {
        if (launchCooldownTimer == null) {
            return false;
        }
        
        return launchCooldownTimer.getSecondsLeft() > 0;
    }
    
    public Timer getLaunchCooldownTimer() {
        return launchCooldownTimer;
    }
    
    public void startLaunchCooldown() {
        launchCooldownTimer = new Timer(new LaunchCooldownCallback(this));
        launchCooldownTimer.setLength(3000);
        launchCooldownTimer.run();
    }
    
    public Timer getParachuteCooldownTimer() {
        return parachuteCooldownTimer;
    }
    
    public void starParachuteCooldown() {
        parachuteCooldownTimer = new Timer(new ParachuteCooldownCallback(this));
        parachuteCooldownTimer.setLength(3000);
        parachuteCooldownTimer.run();
    }
    
    public int getAmmunition() {
        return ammunition;
    }
    
    public void setAmmunition(int ammunition) {
        this.ammunition = ammunition;
    }
    
    public void incrementAmmunition() {
        this.ammunition++;
    }
    
    public void resetLaunchCooldown() {
        if (this.launchCooldownTimer != null) {
            this.launchCooldownTimer.cancel();
            this.launchCooldownTimer = null;
        }
    }
    
    public void resetParachuteCooldown() {
        if (this.parachuteCooldownTimer != null) {
            this.parachuteCooldownTimer.cancel();
            this.parachuteCooldownTimer = null;
        }
    }
}
