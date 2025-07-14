package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameStartWarmupCmd extends GameProgressSubCmd {
    public GameStartWarmupCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "startwarmup", "", "swu");
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

            if (game.getState() == Game.State.TEAMS_ASSIGNED) {
                game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "teleportstart"));
                game.teleportStart();
            }
        }

        if (game.getState() == Game.State.TELEPORT_START_DONE) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "startupwarmupcountdown"));
            game.startWarmup();
            if (game.getState() == Game.State.WARMUP || game.getState() == Game.State.WARMUP_DONE) {
                player.sendMessage(MsgType.INFO.format("The warmup countdown has been started successfully."));
            } else {
                player.sendMessage(MsgType.WARN.format("There was a problem starting the warmup countdown."));
            }
        } else {
            player.sendMessage(MsgType.WARN.format("You must run the teleport players task before starting the countdown."));
        }
    }
}
