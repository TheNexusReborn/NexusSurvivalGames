package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameStartDMCDCmd extends GameProgressSubCmd {
    public GameStartDMCDCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "stardeathmatchcountdown", "", "sdmcd");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() == Game.State.SETTING_UP) {
            MsgType.WARN.send(player, "The game is being set up, please wait for it to finish.");
            return;
        }

        if (!game.getSettings().isAllowDeathmatch()) {
            MsgType.WARN.send(player, "Deathmatch is disabled for this game.");
            return;
        }
        
        if (flagResults.isPresent(PREVIOUS)) {
            if (game.getState() == Game.State.SETUP_COMPLETE) {
                game.assignStartingTeams();
            }

            if (game.getState() == Game.State.TEAMS_ASSIGNED) {
                game.teleportStart();
            }
            
            if (game.getState() == Game.State.TELEPORT_START_DONE || game.getState() == Game.State.WARMUP_DONE) {
                game.startGame();
            }

            if (game.getState() == Game.State.WARMUP) {
                if (game.getTimer() != null) {
                    game.getTimer().cancel();
                } else {
                    player.sendMessage(MsgType.WARN.format("The game state was in warmup countdown, but no timer was actively running. Please report this as a bug, started game anyways."));
                }
                game.startGame();
            }
        }
        
        if (game.getState() == Game.State.INGAME_DEATHMATCH) {
            MsgType.WARN.send(player, "The deathmatch countdown is already running");
            return;
        }

        if (game.getState() == Game.State.INGAME) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "startdeathmatchcountdown"));
            game.startDeathmatchTimer();
            if (game.getState() == Game.State.INGAME_DEATHMATCH) {
                player.sendMessage(MsgType.INFO.format("You started the deathmatch timer"));
            } else {
                player.sendMessage(MsgType.WARN.format("There was a problem starting the deathmatch timer."));
            }
        } else {
            player.sendMessage(MsgType.WARN.format("Invalid state. Please ensure that the game is in the INGAME state."));
        }
    }
}
