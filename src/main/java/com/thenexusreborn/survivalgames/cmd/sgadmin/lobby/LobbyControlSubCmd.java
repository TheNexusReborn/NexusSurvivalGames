package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyControlSubCmd extends SubCommand<SurvivalGames> {
    
    private ControlType controlType;
    
    public LobbyControlSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, ControlType controlType) {
        super(plugin, parent, 1, controlType.name().toLowerCase(), "", Rank.ADMIN, controlType.getAliases());
        this.controlType = controlType;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        
        if (lobby == null) {
            MsgType.WARN.send(sender, "You are not in a lobby.");
            return true;
        }
        
        if (lobby.getControlType() == this.controlType) {
            MsgType.WARN.send(sender, "The lobby is already in %v", this.controlType.name().toLowerCase());
            return true;
        }
        
        lobby.setControlType(controlType);
        lobby.sendMessage(MsgType.INFO.format("The lobby has been set to %v by %v", controlType.name().toLowerCase(), sgPlayer.getColoredName()));
        return true;
    }
}
