package com.thenexusreborn.survivalgames.game;

import com.stardevllc.starlib.time.TimeUnit;
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
        if (game == null || game.getState() == OldGameState.UNDEFINED || game.getState() == OldGameState.ERROR) {
            return null;
        }

        if (game.getState() == OldGameState.SETTING_UP) {
            return "&aSetting up the map...";
        }

        if (game.getState() == OldGameState.SETUP_COMPLETE) {
            return "&aGame Setup.";
        }

        if (game.getState() == OldGameState.ASSIGN_TEAMS) {
            return "&aAssigning starting teams...";
        }

        if (game.getState() == OldGameState.TEAMS_ASSIGNED) {
            return "&aTeams assigned";
        }

        if (game.getState() == OldGameState.TELEPORT_START) {
            return "&aTeleporting players to arena...";
        }

        if (game.getState() == OldGameState.TELEPORT_START_DONE) {
            return "&aTeleport complete.";
        }

        if (game.getState() == OldGameState.WARMUP) {
            return "&fGAME BEGINS IN &c&l" + TimeUnit.SECONDS.fromMillis(game.getTimer().getTime()) + " SECONDS&f...";
        }

        if (game.getState() == OldGameState.WARMUP_DONE) {
            return "&aWarmup done, waiting for game...";
        }

        if (game.getState().ordinal() >= OldGameState.INGAME.ordinal() && game.getState().ordinal() <= OldGameState.DEATHMATCH.ordinal()) {
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
            GamePlayer target = game.getPlayer(mutation.getTarget());
            return "&c&lSEEKING REVENGE ON &a" + target.getName();
        }

        if (game.isGraceperiod()) {
            return "&f&lGrace Period ends in &e" + Game.LONG_TIME_FORMAT.format(game.getGraceperiodTimer().getTime());
        }

        if (game.getState() == OldGameState.INGAME) {
            if (game.willRestockChests()) {
                return "&f&lChests restock in &e" + Game.LONG_TIME_FORMAT.format(game.getNextRestock());
            } else {
                return "&f&lDeathmatch in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime());
            }
        }

        if (game.getState() == OldGameState.INGAME_DEATHMATCH) {
            return "&f&lDeathmatch in &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime());
        }

        if (game.getState() == OldGameState.TELEPORT_DEATHMATCH) {
            return "&aTeleporting to deathmatch...";
        }

        if (game.getState() == OldGameState.TELEPORT_DEATHMATCH_DONE) {
            return "&aDeathmatch teleport completed.";
        }

        if (game.getState() == OldGameState.DEATHMATCH_WARMUP) {
            return "&f&lYou have &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime());
        }

        if (game.getState() == OldGameState.DEATHMATCH_WARMUP_DONE) {
            return "&aDeathmatch warmup complete";
        }

        if (game.getState() == OldGameState.DEATHMATCH) {
            return "&f&lYou have &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTime());
        }

        if (game.getState() == OldGameState.ENDING) {
            if (game.getTimer() == null) {
                return "&aArchiving game...";
            }
            return "&d&lNEXUS&7 - &eNext game starting... &7(" + game.getTimer().getTime() + "s)";
        }

        return "";
    }
}
