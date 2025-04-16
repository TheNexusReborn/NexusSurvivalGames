package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameStartCmd extends GameProgressSubCmd {
    public GameStartCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "start", "", "s");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() == Game.State.SETTING_UP) {
            MsgType.WARN.send(player, "The game is being set up, please wait for it to finish.");
            return;
        }
        
        if (flagResults.isPresent(PREVIOUS)) {
            if (game.getState() == Game.State.SETUP_COMPLETE) {
                game.assignStartingTeams();
            }

            if (game.getState() == Game.State.TEAMS_ASSIGNED) {
                game.teleportStart();
            }
        }

        if (game.getState() == Game.State.WARMUP_DONE || game.getState() == Game.State.TELEPORT_START_DONE) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "start"));
            game.startGame();
        } else if (game.getState() == Game.State.WARMUP) {
            if (game.getTimer() != null) {
                game.getTimer().cancel();
            } else {
                player.sendMessage(MsgType.WARN.format("The game state was in warmup countdown, but no timer was actively running. Please report this as a bug, started game anyways."));
            }
            game.startGame();
        } else if (game.getState() != Game.State.TELEPORT_START_DONE) {
            player.sendMessage(MsgType.WARN.format("You must run the teleport players task at the minimum before starting the game"));
            return;
        }
        if (game.getState() == Game.State.INGAME) {
            player.sendMessage(MsgType.INFO.format("The game has been started."));
        } else {
            player.sendMessage(MsgType.WARN.format("There was a problem starting the game."));
        }
    }
}
