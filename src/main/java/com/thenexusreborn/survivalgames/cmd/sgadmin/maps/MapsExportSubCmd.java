package com.thenexusreborn.survivalgames.cmd.sgadmin.maps;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.YamlMapManager;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import org.bukkit.command.CommandSender;

public class MapsExportSubCmd extends SubCommand<SurvivalGames> {
    public MapsExportSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "export", "", Rank.ADMIN, "e");
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
            SQLMapManager sqlMapManager = new SQLMapManager(plugin);
            for (SGMap map : plugin.getMapManager().getMaps()) {
                sqlMapManager.addMap(map);
            }
            sqlMapManager.saveMaps();
            sender.sendMessage(MsgType.INFO.format("Exported %v maps to SQL.", sqlMapManager.getMaps().size()));
        } else {
            YamlMapManager yamlMapManager = new YamlMapManager(plugin);
            for (SGMap map : plugin.getMapManager().getMaps()) {
                yamlMapManager.addMap(map);
            }
            yamlMapManager.saveMaps();
            sender.sendMessage(MsgType.INFO.format("Exported %v maps to YML.", yamlMapManager.getMaps().size()));
        }
        
        return true;
    }
}
