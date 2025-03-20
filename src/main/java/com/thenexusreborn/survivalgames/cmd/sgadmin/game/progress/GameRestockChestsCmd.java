package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import org.bukkit.entity.Player;

public class GameRestockChestsCmd extends GameProgressSubCmd {
    public GameRestockChestsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setup", "");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
            game.getGameInfo().getActions().add(new GameCmdAction(player.getName(), "restockchests"));
            game.restockChests();
            game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
        } else {
            player.sendMessage(MsgType.WARN.format("Invalid game state. Must be ingame, ingame deathmatch, deathmatch countdown or deathmatch countdown complete."));
        }
    }
}
