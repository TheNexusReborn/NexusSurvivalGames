package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameAssignTeamsCmd extends GameProgressSubCmd {
    public GameAssignTeamsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "assignteams", "", "at");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() == Game.State.SETTING_UP) {
            MsgType.WARN.send(player, "The game is being set up, please wait for it to finish.");
            return;
        }
        
        if (game.getState() == Game.State.UNDEFINED) {
            if (flagResults.isPresent(PREVIOUS)) {
                game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "setup"));
                game.setup();
            }
        }
        
        if (game.getState() == Game.State.SETUP_COMPLETE) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "assignstartingteams"));
            game.assignStartingTeams();
            if (game.getState() == Game.State.TEAMS_ASSIGNED) {
                player.sendMessage(MsgType.INFO.format("Starting teams have been assigned."));
            } else {
                player.sendMessage(MsgType.WARN.format("&cThere was a problem assigning starting teams"));
            }
        } else {
            player.sendMessage(MsgType.WARN.format("The game is not yet setup. Please run the setup task before assigning teams."));
        }
    }
}
