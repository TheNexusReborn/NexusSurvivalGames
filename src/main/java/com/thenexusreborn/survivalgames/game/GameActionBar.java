package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.nexuscore.util.ActionBar;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class GameActionBar extends ActionBar {
    
    private final GamePlayer player;
    private SurvivalGames plugin;
    
    public GameActionBar(SurvivalGames plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    @Override
    public String getText() {
        Game game = plugin.getGame();
        if (game == null || game.getState() == GameState.UNDEFINED || game.getState() == GameState.ERROR) {
            return "";
        }
        
        if (game.getState() == GameState.SETTING_UP) {
            return "&aSetting up the map...";
        }
        
        if (game.getState() == GameState.SETUP_COMPLETE) {
            return "&aGame Setup.";
        }
        
        if (game.getState() == GameState.ASSIGN_TEAMS) {
            return "&aAssigning starting teams...";
        }
        
        if (game.getState() == GameState.TEAMS_ASSIGNED) {
            return "&aTeams assigned";
        }
        
        if (game.getState() == GameState.TELEPORT_START) {
            return "&aTeleporting players to arena...";
        }
        
        if (game.getState() == GameState.TELEPORT_START_DONE) {
            return "&aTeleport complete.";
        }
        
        if (game.getState() == GameState.WARMUP) {
            return "&fGAME BEGINS IN &c&l" + game.getTimer().getSecondsLeft() + " SECONDS&f...";
        }
        
        if (game.getState() == GameState.WARMUP_DONE) {
            return "&aWarmup done, waiting for game...";
        }
    
        //SpigotUtils.sendActionBar(player, "&f&lTARGET: &a" + target.getName() + "   &f&lDISTANCE: &a" + ((int) distance) + "m" + "   &f&lHEALTH: &a" + health + "&f/&a" + maxHealth + " HP");
        
        if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
            TrackerInfo trackerInfo = player.getTrackerInfo();
            if (trackerInfo != null) {
                String target = trackerInfo.getTarget();
                int distance = trackerInfo.getDistance();
                String health = trackerInfo.getHealth();
                String maxHealth = trackerInfo.getMaxHealth();
                return "&f&lTARGET: &a" + target + "   &f&lDISTANCE: &a" + distance + "m" + "   &f&lHEALTH: &a" + health + "&f/&a" + maxHealth + " HP";
            }
        }
        
        if (game.getState() == GameState.INGAME_GRACEPERIOD) {
            return "&f&lGrace Period ends in &e" + Timer.formatTimeShort(game.getGraceperiodTimer().getSecondsLeft());
        }
        
        if (game.getState() == GameState.INGAME) {
            if (game.getRestockTimer() != null) {
                return "&f&lChests restock in &e" + Timer.formatTimeShort(game.getRestockTimer().getSecondsLeft());
            } else {
                return "&f&lDeathmatch in &e" + Timer.formatTime(game.getTimer().getSecondsLeft());
            }
        }
        
        if (game.getState() == GameState.INGAME_DEATHMATCH) {
            return "&f&lDeathmatch in &e" + Timer.formatTime(game.getTimer().getSecondsLeft());
        }
        
        if (game.getState() == GameState.TELEPORT_DEATHMATCH) {
            return "&aTeleporting to deathmatch...";
        }
        
        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
            return "&aDeathmatch teleport completed.";
        }
        
        if (game.getState() == GameState.DEATHMATCH_WARMUP) {
            return "&f&lYou have &e" + Timer.formatTime(game.getTimer().getSecondsLeft());
        }
        
        if (game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
            return "&aDeathmatch warmup complete";
        }
        
        if (game.getState() == GameState.DEATHMATCH) {
            return "&f&lYou have &e" + Timer.formatTime(game.getTimer().getSecondsLeft());
        }
        
        if (game.getState() == GameState.ENDING) {
            if (game.getTimer() == null) {
                return "&aArchiving game...";
            }
            if (!plugin.restart()) {
                return "&d&lNEXUS &7 - &eNext game starting... &7(" + game.getTimer().getSecondsLeft() + "s)";
            } else {
                return "&d&lNEXUS &7 - &cServer Restarting &7(" + game.getTimer().getSecondsLeft() + "s)";
            }
        }
        
        return "";
    }
}
