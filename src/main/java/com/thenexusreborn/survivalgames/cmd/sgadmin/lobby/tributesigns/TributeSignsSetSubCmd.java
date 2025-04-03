package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.tributesigns;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.TributeSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TributeSignsSetSubCmd extends TributeSignsSubCmd {
    public TributeSignsSetSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "set", "", "s");
    }
    
    @Override
    protected boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, Block targetBlock, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            player.sendMessage(MsgType.WARN.format("You must provide an index number."));
            return true;
        }
        
        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(MsgType.WARN.format("You provided an invalid number."));
            return true;
        }
        
        Location signLocation = null, headLocation = null;
        if (targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN) {
            signLocation = targetBlock.getLocation();
        } else if (targetBlock.getType() == Material.SKULL) {
            headLocation = targetBlock.getLocation();
        }
        
        TributeSign tributeSign = null;
        for (TributeSign sign : lobby.getTributeSigns()) {
            if (sign.getIndex() == index) {
                tributeSign = sign;
                break;
            }
        }
        
        if (tributeSign == null) {
            tributeSign = new TributeSign(index, signLocation, headLocation);
            String msg = "You created a new tribute sign with index &b" + index;
            if (signLocation == null) {
                msg += "&e, however you still need to add a sign to it. Just use the same command while looking at a sign.";
            } else {
                msg += "&e, however you still need to add a head to it. Just use the same command while looking at a head.";
            }
            lobby.getTributeSigns().add(tributeSign);
            player.sendMessage(StarColors.color(MsgType.INFO + msg));
            return true;
        }
        
        if (signLocation != null) {
            tributeSign.setSignLocation(signLocation);
            player.sendMessage(MsgType.INFO.format("You set the sign location of the tribute sign at index %v.", index));
        } else if (headLocation != null) {
            tributeSign.setHeadLocation(headLocation);
            player.sendMessage(MsgType.INFO.format("You set the head location of the tribute sign at index %v", index));
        } else {
            player.sendMessage(MsgType.SEVERE.format("Unknown error occured. Please report as a bug."));
            return true;
        }
        return true;
    }
}
