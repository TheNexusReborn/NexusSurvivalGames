package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.statsigns;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.StatSign;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class StatSignsAddSubCmd extends StatSignsSubCmd {
    public StatSignsAddSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "add", "", "a");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, Block sign, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            sender.sendMessage(MsgType.WARN.format("Usage: /survivalgames lobby statsigns add <stat> <displayName>"));
            return true;
        }
        
        String stat = args[0];
        Field field = SGPlayerStats.getFields().get(stat);
        if (field == null) {
            sender.sendMessage(MsgType.WARN.format("You provided an invalid stat name."));
            return true;
        }
        
        for (StatSign statSign : lobby.getStatSigns()) {
            if (statSign.getStat().equalsIgnoreCase(stat)) {
                sender.sendMessage(MsgType.WARN.format("A stat sign with that stat already exists. You can only have one per stat."));
                return true;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        
        String displayName = ChatColor.stripColor(StarColors.color(sb.toString().trim()));
        if (displayName.length() > 14) {
            sender.sendMessage(MsgType.WARN.format("The display name cannot be larger than 14 characters"));
            return true;
        }
        
        StatSign statSign = new StatSign(sign.getLocation(), stat, displayName);
        lobby.getStatSigns().add(statSign);
        sender.sendMessage(MsgType.INFO.format("You added a stat sign for %v with the display name %v.", stat, displayName));
        return true;
    }
}
