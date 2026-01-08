package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.map.SGMap;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.entity.Player;

public class LobbySetMapCmd extends LobbySubCommand {
    public LobbySetMapCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setmap", "", "sm");
    }
    
    @Override
    public boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a map name");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("voting") || args[0].equalsIgnoreCase("reset")) {
            lobby.setGameMap(null);
            sender.sendMessage(MsgType.INFO.format("You reset the map."));
            return true;
        }
        
        SGMap gameMap = plugin.getMapManager().getMap(SGUtils.getMapNameFromCommand(args, 0));
        if (gameMap == null) {
            sender.sendMessage(MsgType.WARN.format("Could not find a map with that name."));
            return true;
        }
        
        if (!gameMap.isValid()) {
            MsgType.WARN.send(sender, "That map does not have a valid configuration.");
            return true;
        }
        
        if (!gameMap.isActive()) {
            sender.sendMessage(MsgType.WARN.format("That map is not active."));
            return true;
        }
        
        lobby.setGameMap(gameMap);
        sender.sendMessage(MsgType.INFO.format("You set the map to %v.", gameMap.getName()));
        return true;
    }
}
