package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KillerInfo {
    protected EntityType type;
    protected double health;
    protected UUID killer;
    protected String teamColor;
    protected ItemStack handItem;
    protected boolean mutationKill;
    protected double distance;
    
    public static KillerInfo createPlayerKiller(GamePlayer killer) {
        Player player = Bukkit.getPlayer(killer.getUniqueId());
        
        KillerInfo killerInfo = new KillerInfo();
        killerInfo.type = EntityType.PLAYER;
        killerInfo.health = player.getHealth();
        killerInfo.teamColor = killer.getTeam().getColor();
        killerInfo.killer = killer.getUniqueId();
        killerInfo.mutationKill = killer.getTeam() == GameTeam.MUTATIONS;
        return killerInfo;
    }
    
    public static KillerInfo createPlayerProjectileKiller(GamePlayer killer, double distance) {
        KillerInfo killerInfo = createPlayerKiller(killer);
        killerInfo.distance = distance;
        return killerInfo;
    }
    
    public static KillerInfo createMobKiller(Entity killer) {
        KillerInfo killerInfo = new KillerInfo();
        killerInfo.type = killer.getType();
        return killerInfo;
    }
    
    public static KillerInfo createMobProjectileKiller(Entity killer, double distance) {
        KillerInfo killerInfo = createMobKiller(killer);
        killerInfo.distance = distance;
        return killerInfo;
    }
    
    public EntityType getType() {
        return type;
    }
    
    public double getHealth() {
        return health;
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    public String getTeamColor() {
        return teamColor;
    }
    
    public ItemStack getHandItem() {
        return handItem;
    }
    
    public boolean isMutationKill() {
        return mutationKill;
    }
    
    public double getDistance() {
        return distance;
    }
}