package com.thenexusreborn.survivalgames.game.state.phase;

import com.stardevllc.starclock.clocks.Timer;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import com.thenexusreborn.survivalgames.game.timer.callbacks.GameSecondsCallback;
import com.thenexusreborn.survivalgames.game.timer.endconditions.WarmupEndCondition;
import org.bukkit.Sound;

public class WarmupPhase extends GamePhase {
    
    private Timer timer;
    
    public WarmupPhase(Game game) {
        super(game, "Warmup");
    }

    @Override
    public void prephase() {
        this.timer = Game.getPlugin().getClockManager().createTimer(TimeUnit.SECONDS.toMillis(game.getSettings().getWarmupLength()) + 50L);
        this.timer.setEndCondition(new WarmupEndCondition(this));
        this.timer.addRepeatingCallback(new GameSecondsCallback(game, "&6&l>> &eThe game begins in &b{time}&e."), TimeUnit.SECONDS, 1);
        this.timer.addCallback(timerSnapshot -> {
            if (game.getSettings().isSounds()) {
                game.playSound(Sound.WOLF_HOWL);
            }
            game.sendMessage("&5&l/ / / / / / &d&lTHE NEXUS REBORN &5&l/ / / / / /");
            game.sendMessage("&6&lSurvival Games &7&oFree-for-all Deathmatch &8- &3Classic Mode");
            game.sendMessage("&8- &7Loot chests scattered around the map for gear.");
            game.sendMessage("&8- &7Outlast the other tributes and be the last one standing!");
            game.sendMessage("&8- &7Arena deathmatch begins after &e" + game.getSettings().getGameLength() + " minutes&7.");
            game.sendMessage("");
            StringBuilder creatorBuilder = new StringBuilder();
            for (String creator : game.getGameMap().getCreators()) {
                creatorBuilder.append("&e").append(creator).append("&7, ");
            }

            if (creatorBuilder.length() < 2) {
                creatorBuilder.append("&eNot Configured, ");
            }

            game.sendMessage("&d&l>> &7Playing on &a" + game.getGameMap().getName() + " &7created by " + creatorBuilder.substring(0, creatorBuilder.length() - 2));
            if (game.getSettings().isGracePeriod()) {
                game.sendMessage("&d&l>> &7There is a &e" + game.getSettings().getGracePeriodLength() + " second &7grace period.");
            }
            setStatus(Status.SENT_GAME_INFO);
        }, TimeUnit.SECONDS.toMillis(game.getSettings().getWarmupLength()) / 2);
    }

    @Override
    public void beginphase() {
        this.timer.start();
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    public enum Status implements PhaseStatus {
        SENT_GAME_INFO
    }
}
