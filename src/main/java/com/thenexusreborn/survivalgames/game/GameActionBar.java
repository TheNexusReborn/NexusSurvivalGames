package com.thenexusreborn.survivalgames.game;

import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.Mutation;

public class GameActionBar implements IActionBar {

    private final GamePlayer player;
    private final SurvivalGames plugin;
    private final SGPlayer sgPlayer;

    public GameActionBar(SurvivalGames plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
    }

    @Override
    public String getText() {
        Game game = sgPlayer.getGame();
        if (game == null || game.getState() == Game.State.UNDEFINED || game.getState() == Game.State.ERROR) {
            return null;
        }

        if (game.getState() == Game.State.SETTING_UP) {
            return "&aSetting up the map...";
        }

        if (game.getState() == Game.State.SETUP_COMPLETE) {
            return "&aGame Setup.";
        }

        if (game.getState() == Game.State.ASSIGN_TEAMS) {
            return "&aAssigning starting teams...";
        }

        if (game.getState() == Game.State.TEAMS_ASSIGNED) {
            return "&aTeams assigned";
        }

        if (game.getState() == Game.State.TELEPORT_START) {
            return "&aTeleporting players to arena...";
        }

        if (game.getState() == Game.State.TELEPORT_START_DONE) {
            return "&aTeleport complete.";
        }

        if (game.getState() == Game.State.WARMUP) {
            return "&fGAME BEGINS IN &c&l" + ((int) TimeUnit.MILLISECONDS.toSeconds(game.getTimer().getTime()) + 1) + " SECONDS&f...";
        }

        if (game.getState() == Game.State.WARMUP_DONE) {
            return "&aWarmup done, waiting for game to start...";
        }

        if (game.getState().ordinal() >= Game.State.INGAME.ordinal() && game.getState().ordinal() <= Game.State.DEATHMATCH.ordinal()) {
            TrackerInfo trackerInfo = player.getTrackerInfo();
            if (trackerInfo != null) {
                String target = trackerInfo.target();
                int distance = trackerInfo.distance();
                String health = trackerInfo.health();
                String maxHealth = trackerInfo.maxHealth();
                return "&f&lTARGET: &a" + target + "   &f&lDISTANCE: &a" + distance + "m" + "   &f&lHEALTH: &a" + health + "&f/&a" + maxHealth + " HP";
            }
        }

        if (player.getTeam() == GameTeam.MUTATIONS) {
            Mutation mutation = player.getMutation();
            if (mutation == null) {
                return "&4&lERROR: GETTING MUTATION INFORMATION, REPORT TO FIRESTAR311";
            }
            GamePlayer target = game.getPlayer(mutation.getTarget());
            
            if (target == null) {
                return "&4&lERROR: GETTING TARGET INFORMATION, REPORT TO FIRESTAR311";
            }
            
            return "&c&lSEEKING REVENGE ON &a" + target.getName();
        }

        if (game.isGraceperiod()) {
            return "&f&lGrace Period ends in &e" + Game.LONG_TIME_FORMAT.format(game.getGraceperiodTimer().getTime());
        }

        if (game.getState() == Game.State.INGAME) {
            if (game.getNextRestock() > 0 && game.getTimedRestockCount() < game.getTotalTimedRestocks()) {
                return "&f&lChests restock in &e" + Game.LONG_TIME_FORMAT.format(game.getNextRestock());
            } else {
                if (game.getSettings().isAllowDeathmatch()) {
                    return "&f&lDeathmatch in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime() + 1000);
                } else {
                    return "&f&lGame ends in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime() + 1000);
                }
            }
        }

        if (game.getState() == Game.State.INGAME_DEATHMATCH) {
            return "&f&lDeathmatch in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime() + 1000);
        }

        if (game.getState() == Game.State.TELEPORT_DEATHMATCH) {
            return "&aTeleporting to deathmatch...";
        }

        if (game.getState() == Game.State.TELEPORT_DEATHMATCH_DONE) {
            return "&aDeathmatch teleport completed.";
        }

        if (game.getState() == Game.State.DEATHMATCH_WARMUP) {
            return "&f&lDeathmatch starts in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime() + 1000);
        }

        if (game.getState() == Game.State.DEATHMATCH_WARMUP_DONE) {
            return "&aDeathmatch warmup complete";
        }

        if (game.getState() == Game.State.DEATHMATCH) {
            return "&f&lGame ends in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime() + 1000);
        }

        if (game.getState() == Game.State.ENDING) {
            if (game.getTimer() == null) {
                return "&aArchiving game...";
            }
            return "&d&lNEXUS&7 - &eNext game starting... &7(" + ((int) TimeUnit.MILLISECONDS.toSeconds(game.getTimer().getTime()) + 1) + "s)";
        }

        return "";
    }
}
