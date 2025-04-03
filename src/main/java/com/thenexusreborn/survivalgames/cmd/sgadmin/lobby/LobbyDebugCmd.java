package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.entity.Player;

public class LobbyDebugCmd extends LobbySubCommand {
    public LobbyDebugCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "debug", "");
    }
    
    @Override
    public boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        if (lobby.isDebugMode()) {
            lobby.disableDebug();
            sender.sendMessage(MsgType.INFO.format("You disabled lobby debug mode."));
        } else {
            lobby.enableDebug();
            sender.sendMessage(MsgType.INFO.format("You &aenabled lobby debug mode."));
        }
        return true;
    }
}
