package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameNextGameCmd extends GameProgressSubCmd {
    public GameNextGameCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setup", "");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (flagResults.isPresent(PREVIOUS)) {
            game.end();
        }
        
        if (game.getState() == Game.State.ENDING || game.getState() == Game.State.ENDED) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "nextgame"));
            game.nextGame();
            player.sendMessage(MsgType.INFO.format("Moved everyone to the next game"));
        } else {
            player.sendMessage(MsgType.WARN.format("You must end the game first before going to the next one."));
        }
    }
}
