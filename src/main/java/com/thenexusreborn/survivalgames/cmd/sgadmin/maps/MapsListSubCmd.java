package com.thenexusreborn.survivalgames.cmd.sgadmin.maps;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.CommandSender;

public class MapsListSubCmd extends SubCommand<SurvivalGames> {
    public MapsListSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "list", "", Rank.ADMIN, "l");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (plugin.getMapManager().getMaps().isEmpty()) {
            MsgType.WARN.send(sender, "There are no maps configured.");
            return true;
        }
        
        StarColors.coloredMessage(sender, "&6&l>> &eList of &bSG Maps");
        for (SGMap map : plugin.getMapManager().getMaps()) {
            StarColors.coloredMessage(sender, " &6&l> &d" + map.getName() + "  " + (map.isActive() ? "&a&lACTIVE" : "&c&lINACTIVE") + "  " + (map.isValid() ? "&a&lVALID" : "&c&lINVALID"));
        }
        return true;
    }
}
