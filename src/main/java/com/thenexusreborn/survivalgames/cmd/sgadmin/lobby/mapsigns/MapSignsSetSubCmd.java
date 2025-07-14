package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.mapsigns;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MapSignsSetSubCmd extends MapSignsSubCmd {
    public MapSignsSetSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "set", "", "s");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, Block sign, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("You must provide a position number."));
            return true;
        }
        
        int position;
        try {
            position = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MsgType.WARN.format("You provided an invalid number."));
            return true;
        }
        
        lobby.getMapSigns().put(position, sign.getLocation());
        sender.sendMessage(MsgType.INFO.format("You set the sign you are looking at as a map sign in position %v.", position));
        lobby.generateMapOptions();
        return true;
    }
}
