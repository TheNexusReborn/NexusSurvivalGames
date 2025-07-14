package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.entity.Player;

public class LobbyForceStartCmd extends LobbySubCommand {
    public LobbyForceStartCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "forcestart", "", "fs");
    }
    
    @Override
    public boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            player.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        String message = switch (lobby.getState()) {
            case SHUTTING_DOWN -> MsgType.WARN.format("You cannot force start while lobby is shutting down");
            case WAITING -> null;
            case COUNTDOWN -> MsgType.WARN.format("You cannot force start the lobby as it is already started");
            case STARTING -> MsgType.WARN.format("You cannot force start the lobby while it is starting a game");
            case PREPARING_GAME, GAME_PREPARED -> MsgType.WARN.format("You cannot force start the lobby while it is preparing a game");
            case MAP_CONFIGURATING -> MsgType.WARN.format("You cannot force start the lobby while editing maps");
            case SETUP -> MsgType.WARN.format("You cannot force start the lobby while it is setting up itself");
        };
        
        if (lobby.getState() != LobbyState.WAITING) {
            player.sendMessage(message);
            return true;
        }
        
        lobby.forceStart();
        lobby.sendMessage(MsgType.INFO.format("The lobby has been forcefully started by %v", sgPlayer.getColoredName()));
        return true;
    }
}
