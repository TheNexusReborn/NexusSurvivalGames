package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameTPPlayersCmd extends GameProgressSubCmd {
    public GameTPPlayersCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "teleportplayers", "", "tpp");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() == Game.State.SETTING_UP) {
            MsgType.WARN.send(player, "The game is being set up, please wait for it to finish.");
            return;
        }
        
        if (flagResults.isPresent(PREVIOUS)) {
            if (game.getState() == Game.State.SETUP_COMPLETE) {
                game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "assignstartingteams"));
                game.assignStartingTeams();
            }
        }

        if (game.getState() == Game.State.TEAMS_ASSIGNED) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "teleportplayers"));
            game.teleportStart();
            if (game.getState() == Game.State.TELEPORT_START_DONE) {
                player.sendMessage(MsgType.INFO.format("Players have been teleported."));
            } else {
                player.sendMessage(MsgType.WARN.format("There was a problem teleporting players."));
            }
        } else {
            player.sendMessage(MsgType.WARN.format("The teams have not be assigned yet. Please run the team assignment task."));
        }
    }
}
