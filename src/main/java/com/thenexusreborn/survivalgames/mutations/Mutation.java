package com.thenexusreborn.survivalgames.mutations;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.timer.MutationCountdownCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.UUID;

public abstract class Mutation {
    
    protected static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    protected final MutationType type;
    protected final UUID player;
    protected UUID target;
    protected Timer countdownTimer;
    
    public static Mutation createInstance(MutationType type, UUID player, UUID target) {
        Class<? extends Mutation> clazz = type.getClazz();
        Constructor<? extends Mutation> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(UUID.class, UUID.class);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("Mutation Class " + clazz.getName() + " does not have the constructor (UUID player, UUID target)");
            return null;
        }
        
        constructor.setAccessible(true);
    
        try {
            return constructor.newInstance(player, target);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().severe("Could not create an instance of the Mutation Class: " + clazz.getName());
            plugin.getLogger().severe("Error Message: " + e.getClass().getName() + ": " + e.getMessage());
        }
        
        return null;
    }
    
    protected Mutation(MutationType type, UUID player, UUID target) {
        this.type = type;
        this.player = player;
        this.target = target;
    }
    
    public MutationType getType() {
        return type;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public void startCountdown() {
        Player p = Bukkit.getPlayer(this.player);
        if (p == null) {
            return;
        }
        
        this.countdownTimer = new Timer(new MutationCountdownCallback(this));
        this.countdownTimer.setLength(plugin.getGame().getSettings().getMutationSpawnDelay() * 1000L + 50);
        p.sendMessage(MCUtils.color("&6&l>> &eYou will mutate as a(n) " + getType().getDisplayName() + "!"));
        p.sendMessage(MCUtils.color("&6&l>> &eYou will be mutated in &l" + this.countdownTimer.getSecondsLeft() + " Seconds&e."));
        Player t = Bukkit.getPlayer(this.target);
        t.sendMessage(MCUtils.color("&4&l>> &c" + p.getName() + " is &lMUTATING! &cThey spawn in &c&l" + this.countdownTimer.getSecondsLeft() + "s..."));
        this.countdownTimer.run();
    }
    
    public void setTarget(UUID uniqueId) {
        this.target = uniqueId;
    }
}
