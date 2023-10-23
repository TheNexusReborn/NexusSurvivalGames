package com.thenexusreborn.survivalgames.game.timer.old;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.nexuscore.util.timer.*;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.Sound;

import java.util.*;

public class CountdownTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 3, 2, 1));
    
    private final Game game;
    private final Set<Integer> announced = new HashSet<>();
    private final Set<Integer> soundPlayed = new HashSet<>();
    private boolean announcedInfo = false, announcedRestart = false;
    
    public CountdownTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        int remainingSeconds = timerSnapshot.getSecondsLeft();
        if (remainingSeconds <= 0) {
            if (Game.getControlType() == ControlType.AUTOMATIC) {
                game.startGame();
            } else {
                game.warmupComplete();
                game.sendMessage("&eThe timer concluded but the mode is not automatic. Waiting for the command to start game.");
            }
            return false;
        }
        
        if (remainingSeconds == game.getSettings().getWarmupLength() / 2) {
            if (!announcedInfo) {
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
                
                announcedInfo = true;
            }
        }
        
        if (remainingSeconds == game.getSettings().getWarmupLength() - 10) {
            if (!announcedRestart) {
                if (SurvivalGames.getPlugin(SurvivalGames.class).restart()) {
                    game.sendMessage("&6&l>> &e&lTHE SERVER WILL RESTART AFTER THIS GAME.");
                    this.announcedRestart = true;
                }
            }
        }
        
        if (game.getSettings().isSounds()) {
            if (!soundPlayed.contains(remainingSeconds)) {
                game.playSound(Sound.NOTE_BASS);
                this.soundPlayed.add(remainingSeconds);
            }
        }
        
        if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
            if (!this.announced.contains(remainingSeconds)) {
                game.sendMessage("&6&l>> &eThe game begins in &b" + Game.LONG_TIME_FORMAT.format(timerSnapshot.getTimeLeft()) + "&e.");
                if (game.getSettings().isSounds()) {
                    game.playSound(Sound.CLICK);
                }
                this.announced.add(remainingSeconds);
            }
        }
        return true;
    }
}
