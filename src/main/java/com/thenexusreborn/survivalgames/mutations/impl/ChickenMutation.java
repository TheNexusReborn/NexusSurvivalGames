package com.thenexusreborn.survivalgames.mutations.impl;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.MutationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
        
        return launchCooldownTimer.getTime() > 0;
    }
    
    public boolean isParachuteOnCooldown() {
        if (parachuteCooldownTimer == null) {
            return false;
        }
        
        return parachuteCooldownTimer.getTime() > 0;
    }
    
    public Timer getLaunchCooldownTimer() {
        return launchCooldownTimer;
    }
    
    public void startLaunchCooldown() {
        if (launchCooldownTimer == null) {
            launchCooldownTimer = Game.getPlugin().getClockManager().createTimer(5000L);
            launchCooldownTimer.addCallback(timerSnapshot -> Bukkit.getPlayer(getPlayer()).sendMessage(ColorHandler.getInstance().color(MsgType.INFO + "Chicken Launch is ready!")), 0L);
            launchCooldownTimer.start();
        } else {
            launchCooldownTimer.reset();
        }
    }
    
    public Timer getParachuteCooldownTimer() {
        return parachuteCooldownTimer;
    }
    
    public void startParachuteCooldown() {
        if (parachuteCooldownTimer == null) {
            parachuteCooldownTimer = Game.getPlugin().getClockManager().createTimer(5000L);
            parachuteCooldownTimer.addCallback(timerSnapshot -> Bukkit.getPlayer(getPlayer()).sendMessage(ColorHandler.getInstance().color(MsgType.INFO + "Chicken Chute is ready!")), 0L);
            parachuteCooldownTimer.start();
        } else {
            parachuteCooldownTimer.reset();
        }
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
