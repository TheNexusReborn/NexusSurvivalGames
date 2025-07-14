package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.statsigns;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.StatSign;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class StatSignsRemoveSubCmd extends StatSignsSubCmd {
    public StatSignsRemoveSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "remove", "", "r");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, Block sign, String[] args, FlagResult flagResults) {
        Iterator<StatSign> iterator = lobby.getStatSigns().iterator();
        while (iterator.hasNext()) {
            StatSign entry = iterator.next();
            if (entry.getLocation().equals(sign.getLocation())) {
                iterator.remove();
                sender.sendMessage(MsgType.INFO.format("You removed that stat sign"));
                break;
            }
        }
        return true;
    }
}
