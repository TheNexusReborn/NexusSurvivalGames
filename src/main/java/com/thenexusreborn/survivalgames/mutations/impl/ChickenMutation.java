package com.thenexusreborn.survivalgames.mutations.impl;

import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.timer.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class ChickenMutation extends Mutation {
    
    private Timer launchCooldownTimer;
    private Timer parachuteCooldownTimer;
    
    private int ammunition;
    private boolean chuteActive;
    
    private List<Entity> chickens = new ArrayList<>();
    
    protected ChickenMutation(Game game, UUID player, UUID target) {
        super(game, MutationType.CHICKEN, player, target);
    }
    
    public boolean isLaunchOnCooldown() {
        if (launchCooldownTimer == null) {
            return false;
        }
        
        return launchCooldownTimer.getSecondsLeft() > 0;
    }
    
    public boolean isParachuteOnCooldown() {
        if (parachuteCooldownTimer == null) {
            return false;
        }
        
        return parachuteCooldownTimer.getSecondsLeft() > 0;
    }
    
    public Timer getLaunchCooldownTimer() {
        return launchCooldownTimer;
    }
    
    public void startLaunchCooldown() {
        launchCooldownTimer = new Timer(new LaunchCooldownCallback(this));
        launchCooldownTimer.setLength(5000);
        launchCooldownTimer.run();
    }
    
    public Timer getParachuteCooldownTimer() {
        return parachuteCooldownTimer;
    }
    
    public void startParachuteCooldown() {
        parachuteCooldownTimer = new Timer(new ParachuteCooldownCallback(this));
        parachuteCooldownTimer.setLength(5000);
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
    
    public void decrementAmmunition() {
        this.ammunition--;
    }
    
    public boolean isChuteActive() {
        return chuteActive;
    }
    
    public void deactivateChute() {
        this.chuteActive = false;
        for (Entity chicken : this.chickens) {
            chicken.remove();
        }
        
        this.chickens.clear();
    }
    
    public void activateChute() {
        this.chuteActive = true;
        Player p = Bukkit.getPlayer(this.player);
        World world = p.getWorld();
        Location spawnLocation = p.getLocation().add(0, 3, 0);
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            Location loc = spawnLocation.clone();
            double x = rand.nextInt(1) + rand.nextDouble();
            double z = rand.nextInt(1) + rand.nextDouble();
            if (rand.nextInt(10) < 5) {
                loc.add(x, 0, z);
            } else {
                loc.subtract(x, 0, z);
            }
            Chicken chicken = (Chicken) world.spawnEntity(loc, EntityType.CHICKEN);
            chicken.setLeashHolder(p);
            this.chickens.add(chicken);
        }
    }
}
