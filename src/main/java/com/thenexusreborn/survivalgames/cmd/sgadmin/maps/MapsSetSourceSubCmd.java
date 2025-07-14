package com.thenexusreborn.survivalgames.cmd.sgadmin.maps;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.YamlMapManager;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import org.bukkit.command.CommandSender;

public class MapsSetSourceSubCmd extends SubCommand<SurvivalGames> {
    public MapsSetSourceSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "setsource", "", Rank.ADMIN, "ss");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a data type, SQL or YML");
            return true;
        }
        
        String option = args[0];
        if (!(option.equalsIgnoreCase("sql") || option.equalsIgnoreCase("yml"))) {
            sender.sendMessage(MsgType.WARN.format("Invalid option, only valid options are sql and yml."));
            return true;
        }
        
        if (option.equalsIgnoreCase("sql")) {
            if (plugin.getMapManager() instanceof SQLMapManager) {
                sender.sendMessage(MsgType.WARN.format("The map souce is already set to SQL."));
                return true;
            }
            
            plugin.setMapManager(new SQLMapManager(plugin));
            sender.sendMessage(MsgType.INFO.format("You set the map source to SQL."));
        } else {
            if (plugin.getMapManager() instanceof YamlMapManager) {
                sender.sendMessage(MsgType.WARN.format("The map souce is already set to YML."));
                return true;
            }
            
            plugin.setMapManager(new YamlMapManager(plugin));
            sender.sendMessage(MsgType.INFO.format("You set the map source to YML."));
        }
        
        return true;
    }
}
