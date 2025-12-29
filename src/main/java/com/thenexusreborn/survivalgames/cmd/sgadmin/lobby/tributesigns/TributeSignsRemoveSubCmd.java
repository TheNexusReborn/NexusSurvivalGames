package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.tributesigns;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.TributeSign;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class TributeSignsRemoveSubCmd extends TributeSignsSubCmd {
    public TributeSignsRemoveSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "remove", "", "r");
    }
    
    @Override
    protected boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, Block targetBlock, String[] args, FlagResult flagResults) {
        Iterator<TributeSign> iterator = lobby.getTributeSigns().iterator();
        while (iterator.hasNext()) {
            TributeSign sign = iterator.next();
            if (sign.getSignLocation().equals(targetBlock.getLocation()) || sign.getHeadLocation().equals(targetBlock.getLocation())) {
                iterator.remove();
                player.sendMessage(MsgType.INFO.format("You removed the tribute sign with index %v.", sign.getIndex()));
            }
        }
        
        return true;
    }
}
