package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starlib.helper.StringHelper;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.SGMode;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.entity.Player;

public class LobbyModeCmd extends LobbySubCommand {
    public LobbyModeCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "mode", "Set the mode of the game");
    }
    
    @Override
    public boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            sgPlayer.sendMessage(MsgType.WARN + "You must provide a mode name");
            return true;
        }
        
        SGMode mode;
        try {
            mode = SGMode.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sgPlayer.sendMessage(MsgType.WARN.format("Invalid mode %v.", args[0]));
            return true;
        }
        
        lobby.setMode(mode);
        lobby.sendMessage(MsgType.INFO + sgPlayer.getTrueColoredName() + " &eset the mode to &b" + StringHelper.titlize(mode.name()));
        
        return true;
    }
}
