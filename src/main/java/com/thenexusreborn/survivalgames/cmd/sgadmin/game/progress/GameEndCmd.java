package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameEndCmd extends GameProgressSubCmd {
    public GameEndCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "end", "");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() != Game.State.ENDING && game.getState() != Game.State.ENDED) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "end"));
            game.end();
            player.sendMessage(MsgType.INFO.format("You ended the game."));
        } else {
            player.sendMessage(MsgType.WARN.format("The game has already ended"));
        }
    }
}
