package com.thenexusreborn.survivalgames.mutations;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.timer.MutationCountdownCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Mutation {
    
    protected static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    protected final MutationType type;
    protected final UUID player;
    protected final UUID target;
    protected Timer countdownTimer;
    
    public Mutation(MutationType type, UUID player, UUID target) {
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
    
    public void remove() {
        //TODO This handles the removal of the mutation from the game, this will have to account for a null Bukkit player
    }
    
    public void success() {
        //TODO This handles when a mutation is successful in taking revenge
    }
    
    public void reassignTarget() {
        //TODO This handles reassigning the target of the mutation in case their target dies to another player, use remove() if target dies to other means
    }
    
    public void deathmatch() {
        //TODO This handles for when a mutation lasts until deathmatch
    }
    
    public void startCountdown() {
        Player p = Bukkit.getPlayer(this.player);
        if (p == null) {
            return;
        }
        
        this.countdownTimer = new Timer(new MutationCountdownCallback(this));
        this.countdownTimer.setLength(10050);
        p.sendMessage(MCUtils.color("&6&l>> &eYou will mutate as a(n) " + getType().getDisplayName() + "!"));
        p.sendMessage(MCUtils.color("&6&l>> &eYou will be mutated in &l" + this.countdownTimer.getSecondsLeft() + " Seconds&e."));
        this.countdownTimer.run();
    }
}
