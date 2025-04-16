package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.entity.Player;

public class LobbyPrepareGameCmd extends LobbySubCommand {
    public LobbyPrepareGameCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "preparegame", "", "pg");
    }
    
    @Override
    public boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        LobbyState lobbyState = lobby.getState();
        if (lobbyState == LobbyState.WAITING || lobbyState == LobbyState.COUNTDOWN) {
            lobby.prepareGame();
            sender.sendMessage(MsgType.INFO.format("You forcefully had the lobby prepare the game."));
        } else {
            sender.sendMessage(MsgType.WARN.format("The lobby is in an invalid state to prepare a game."));
            return true;
        }
        return true;
    }
}
