package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.mapsigns;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map.Entry;

public class MapSignsRemoveSubCmd extends MapSignsSubCmd {
    public MapSignsRemoveSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "remove", "", "r");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, Block sign, String[] args, FlagResult flagResults) {
        Iterator<Entry<Integer, Location>> iterator = lobby.getMapSigns().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Location> entry = iterator.next();
            if (entry.getValue().equals(sign.getLocation())) {
                iterator.remove();
                sender.sendMessage(MsgType.WARN.format("You removed the sign with the position %v.", entry.getKey()));
                break;
            }
        }
        
        return true;
    }
}
