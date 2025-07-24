package com.thenexusreborn.survivalgames.game;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.stardevllc.starlib.time.TimeUnit;

import java.util.UUID;

public final class CombatTag {
    private UUID player, other;
    private Game game;
    private Timer timer;
    
    public CombatTag(Game game, UUID player) {
        this.game = game;
        this.player = player;
        this.timer = Game.getPlugin().getClockManager().createTimer(TimeUnit.SECONDS.toMillis(game.getSettings().getCombatTagLength()));
        this.timer.addCallback(timerSnapshot -> {
            if (other != null) {
                setOther(null);
                GamePlayer gamePlayer = this.game.getPlayer(player);
                if (gamePlayer != null) {
                    gamePlayer.sendMessage("&6&l>> &eYou are no longer in combat.");
                }
            }
        }, 0L);
        
        this.timer.setEndCondition(timerSnapshot -> game.getPlayer(player) == null);
        timer.start();
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public UUID getOther() {
        return other;
    }
    
    public void setOther(UUID other) {
        this.other = other;
        if (other != null) {
            this.timer.reset();
        }
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isInCombatWith(UUID uuid) {
        if (!isInCombat()) {
            return false;
        }
        
        return this.other.equals(uuid);
    }
    
    public boolean isInCombat() {
        return this.other != null && this.timer.getTime() > 0;
    }

    @Override
    public String toString() {
        return "CombatTag{" +
                "player=" + player +
                ", other=" + other +
                ", game=" + game +
                ", timer=" + timer +
                '}';
    }
}
