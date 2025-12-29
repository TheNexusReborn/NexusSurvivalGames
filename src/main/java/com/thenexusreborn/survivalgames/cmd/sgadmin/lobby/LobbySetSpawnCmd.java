package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.entity.Player;

public class LobbySetSpawnCmd extends LobbySubCommand {
    public LobbySetSpawnCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setspawn", "", "ss");
    }
    
    @Override
    public boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        lobby.setSpawnpoint(sender.getLocation());
        sender.sendMessage(MsgType.INFO.format("You set the lobby spawnpoint to your location."));
        return true;
    }
}
